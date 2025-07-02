package uk.gov.hmcts.reform.em.hrs.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HearingRecordingSegmentConsumerPactTest {

    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    private static final String SERVICE_AUTH_TOKEN = "Bearer someServiceAuthorizationToken";
    private static final String PROVIDER = "em_hrs_api_recording_segments_provider";
    private static final String CONSUMER = "em_hrs_api_recording_segments_consumer";
    private static final String POST_SEGMENT_API_PATH = "/segments";

    public Map<String, String> getHeaders() {
        return Map.of(
            AUTHORIZATION, AUTH_TOKEN,
            "ServiceAuthorization", SERVICE_AUTH_TOKEN,
            "Content-Type", "application/json"
        );
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    public V4Pact createHearingRecording202(PactDslWithProvider builder) {
        DslPart requestBody = LambdaDsl.newJsonBody(body -> {
            body.stringType("folder", "some-folder");
            body.stringType("caseRef", "1234567890123456");
            body.object("recordingSource", source -> {
                source.stringType("source", "CVP");
            });
            body.stringType("hearingRoomRef", "room-1");
            body.stringType("serviceCode", "BBA3");
            body.stringType("jurisdictionCode", "BBA");
            body.stringType("courtLocationCode", "123");
            body.stringType("recordingRef", "rec-123456");
            body.stringType("sourceBlobUrl", "http://blobstorage/rec-123456");
            body.stringType("filename", "file.mp3");
            body.stringType("filenameExtension", "mp3");
            body.numberType("fileSize", 12345678L);
            body.integerType("segment", 1);
            body.stringType("checkSum", "abc123");
            body.stringType("interpreter", "none");
            body.stringType("recordingDateTime", "2025-07-02-10.30.00.000");
        }).build();

        return builder
            .given("Ready to accept a new hearing recording segment")
            .uponReceiving("A valid post request for a hearing recording segment")
            .path(POST_SEGMENT_API_PATH)
            .method("POST")
            .headers(getHeaders())
            .body(requestBody)
            .willRespondWith()
            .status(HttpStatus.ACCEPTED.value())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createHearingRecording202", providerName = PROVIDER)
    void testCreateHearingRecording202(MockServer mockServer) {
        String requestBody = """
        {
          "folder": "some-folder",
          "caseRef": "1234567890123456",
          "recordingSource": {"source": "CVP"},
          "hearingRoomRef": "room-1",
          "serviceCode": "BBA3",
          "jurisdictionCode": "BBA",
          "courtLocationCode": "123",
          "recordingRef": "rec-123456",
          "sourceBlobUrl": "http://blobstorage/rec-123456",
          "filename": "file.mp3",
          "filenameExtension": "mp3",
          "fileSize": 12345678,
          "segment": 1,
          "checkSum": "abc123",
          "interpreter": "none",
          "recordingDateTime": "2025-07-02-10.30.00.000"
        }
        """;

        io.restassured.RestAssured
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post(mockServer.getUrl() + POST_SEGMENT_API_PATH)
            .then()
            .log().all()
            .statusCode(HttpStatus.ACCEPTED.value());
    }


}

