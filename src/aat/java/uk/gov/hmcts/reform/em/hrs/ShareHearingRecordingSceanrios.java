package uk.gov.hmcts.reform.em.hrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.hrs.testutil.CcdAuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {EmTestConfig.class, CcdAuthTokenGeneratorConfiguration.class, ExtendedCcdHelper.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ShareHearingRecordingSceanrios {
    @Autowired
    protected ExtendedCcdHelper extendedCcdHelper;

    @Inject
    AuthTokenGenerator authTokenGenerator;

    @Inject
    private IdamHelper idamHelper;

    @Value("${test.url}")
    private String testUrl;

    public static final Long CCD_CASE_ID = 1111111L;
    public static final String SHAREE_EMAIL_ADDRESS = "sharee.tester@test.com";
    public static final String ERROR_SHAREE_EMAIL_ADDRESS = "sharee.testertest.com";

    @Before
    public void init() {
        idamHelper.createUser("a@b.com", Stream.of("caseworker").collect(Collectors.toList()));
    }

    @Test
    @Ignore("The CCD Case Id is not Working as part of a Segment Post")
    public void testShareRecording_success_scenario() throws Exception {

        final CaseDetails request = CaseDetails.builder()
            .data(Map.of("recipientEmailAddress", SHAREE_EMAIL_ADDRESS))
            .id(12345L)
            .build();
        sendRequest(request,202);

    }

    @Test
    public void testShareRecording_negative_non_existent_ccd_case_id() throws Exception {

        final CaseDetails request = CaseDetails.builder()
            .data(Map.of("recipientEmailAddress", SHAREE_EMAIL_ADDRESS))
            .id(11111L)
            .build();
        sendRequest(request,404);

    }

    @Test
    @Ignore("The CCD Case Id is not Working as part of a Segment Post")
    public void testShareRecording_negative_non_existent_email_id() throws Exception {

        final CaseDetails request = CaseDetails.builder()
            .data(Map.of("recipientEmailAddress", ERROR_SHAREE_EMAIL_ADDRESS))
            .id(11111L)
            .build();
        sendRequest(request,500);
    }

    private void sendRequest(CaseDetails request,int statusCode) throws IOException {
        RestAssured
            .given()
            .header("Authorization", idamHelper.authenticateUser("a@b.com"))
            .header("ServiceAuthorization", authTokenGenerator.generate())
            .relaxedHTTPSValidation()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .body(convertObjectToJsonString(request))
            .when()
            .post("/sharees")
            .then()
            .statusCode(statusCode).log().all();
    }

    public static String convertObjectToJsonString(Object object) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        return objectMapper.writeValueAsString(object);
    }
}

