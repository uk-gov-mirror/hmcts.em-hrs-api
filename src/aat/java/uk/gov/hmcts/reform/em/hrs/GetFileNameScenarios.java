package uk.gov.hmcts.reform.em.hrs;

import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.hrs.testutil.AuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.hrs.testutil.CcdAuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;
import uk.gov.hmcts.reform.em.hrs.testutil.UploadToCVPBlobstore;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;

import java.util.List;
import java.util.UUID;
import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {EmTestConfig.class, CcdAuthTokenGeneratorConfiguration.class, ExtendedCcdHelper.class, AuthTokenGeneratorConfiguration.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringJUnit4ClassRunner.class)
public class GetFileNameScenarios {

    @Autowired
    protected ExtendedCcdHelper extendedCcdHelper;

    @Autowired
    AuthTokenGenerator authTokenGenerator;

    @Autowired
    private IdamHelper idamHelper;

    @Value("${test.url}")
    private String testUrl;

    @Test
    public void testGetFileNames() throws Exception {


// upload file to the cvp blobstore and then use the blobstore url


        JsonNode reqBody = extendedCcdHelper.createRecordingSegment(
            "audiostream01",
            "http://localhost:10000/devstoreaccount1/cvptestcontainer/audiostream01/download.jpeg",
            "audiostream01/download.jpeg",
            "jpeg",
            226200L,
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

        Thread.sleep(10000);

        ValidatableResponse response = RestAssured
            .given()
            .header("Authorization", idamHelper.authenticateUser("a@b.com"))
            .header("ServiceAuthorization", authTokenGenerator.generate())
            .relaxedHTTPSValidation()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .when()
            .get("/folders/audiostream01")
            .then()
            .statusCode(200).log().all();


        assertEquals("audiostream01",response.extract().body().jsonPath().get("folder-name"));
        List<String> fileNames = response.extract().body().jsonPath().get("filenames");
        assertTrue(fileNames.stream().anyMatch(s -> s.equals("audiostream01/download.jpeg")));
    }
}
