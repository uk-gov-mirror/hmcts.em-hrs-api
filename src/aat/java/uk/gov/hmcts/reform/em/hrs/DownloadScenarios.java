package uk.gov.hmcts.reform.em.hrs;


import io.restassured.RestAssured;
import org.junit.Test;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class DownloadScenarios {

    @Test
    public void testGetHearingRecording() {
        RestAssured
            .given()
            .baseUri("http://localhost:8080")
            .contentType(APPLICATION_JSON_VALUE)
            .get("/documents/hearing-recordings/TEST-REF/segments/TEST-SEGMENT");
    }
}
