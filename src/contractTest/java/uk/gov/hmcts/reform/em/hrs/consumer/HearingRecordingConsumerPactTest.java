package uk.gov.hmcts.reform.em.hrs.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HearingRecordingConsumerPactTest {

    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    private static final String SERVICE_AUTH_TOKEN = "Bearer someServiceAuthorizationToken";
    private static final String POST_SEGMENTS_PATH = "/segments";

    public Map<String, String> getHeaders() {
        return Map.of(
            AUTHORIZATION, AUTH_TOKEN,
            "ServiceAuthorization", SERVICE_AUTH_TOKEN
        );
    }

    @Pact(consumer = "em_hrs_recording_api_consumer", provider = "em_hrs_recording_api")
    public V4Pact deleteHearingRecordingPact(PactBuilder builder) {
        return builder
            .usingLegacyDsl()
            .given("Hearing recordings exist for given CCD case IDs to delete")
            .uponReceiving("A request to delete hearing recordings for a case")
            .path("/delete")
            .query("case_id=123456789")
            .method("DELETE")
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.NO_CONTENT.value())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "deleteHearingRecordingPact")
    void testDeleteHearingRecordings(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .delete(mockServer.getUrl() + "/hearing-recordings?case_id=123456789")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .body(equalTo(""));
    }
}
