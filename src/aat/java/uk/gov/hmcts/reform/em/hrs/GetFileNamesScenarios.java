package uk.gov.hmcts.reform.em.hrs;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
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
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;

import java.util.List;
import java.util.UUID;
import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
    @Disabled("To be enabled when the Code is ready for Testing...")
    public void testGetFileNames() throws Exception {

        UUID id = UUID.randomUUID();
        HearingRecordingDto reqBody = extendedCcdHelper.createRecordingSegment(
            "http://dm-store:8080/documents/e486435e-30e8-456c-9d4d-4adffcb50010",
            "functional-tests/hearing-recording-segment"+id.toString(),
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
            .statusCode(202).log().all();

        Thread.sleep(3000);

        ValidatableResponse response = RestAssured
            .given()
            .header("Authorization", idamHelper.authenticateUser("a@b.com"))
            .header("ServiceAuthorization", authTokenGenerator.generate())
            .relaxedHTTPSValidation()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .when()
            .get("/folders/functional-tests")
            .then()
            .statusCode(200).log().all();
        assertEquals("functional-tests",response.extract().body().jsonPath().get("folderName"));
        List<String> fileNames = response.extract().body().jsonPath().get("filenames");
        assertTrue(fileNames.stream().anyMatch(s -> {
            return s.equals("hearing-recording-segment"+id.toString()+"mp4");
        }));
    }
}
