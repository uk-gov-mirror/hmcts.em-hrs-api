package uk.gov.hmcts.reform.em.hrs;

import io.restassured.RestAssured;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.testutil.AuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.hrs.testutil.CcdAuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;

import javax.inject.Inject;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {EmTestConfig.class, AuthTokenGeneratorConfiguration.class, ExtendedCcdHelper.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringJUnit4ClassRunner.class)
public class GetFileNamesScenarios {

    @Autowired
    protected ExtendedCcdHelper extendedCcdHelper;

    @Inject
    AuthTokenGenerator authTokenGenerator;

    @Inject
    private IdamHelper idamHelper;

    @Value("${test.url}")
    private String testUrl;

    @Test
    public void test() {

        HearingRecordingDto reqBody = extendedCcdHelper.createRecordingSegment(
            "http://dm-store:8080/documents/e486435e-30e8-456c-9d4d-4adffcb50010",
            "functional-tests/hearing-recording-segment.test",
            ".mp4",
            12L,
            0
        );

        RestAssured
            .given()
            .header("Authorization", idamHelper.authenticateUser("a@b.com"))
            .header("ServiceAuthorization", authTokenGenerator.generate())
            .relaxedHTTPSValidation()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .body(reqBody)
            .when()
            .post("/segments")
            .then()
            .statusCode(202);



        RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .when()
            .get("/folders/functional-tests")
            .then()
            .statusCode(200);

    }
}
