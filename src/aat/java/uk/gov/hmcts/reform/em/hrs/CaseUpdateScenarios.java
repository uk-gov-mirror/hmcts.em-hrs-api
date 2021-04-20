package uk.gov.hmcts.reform.em.hrs;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CaseUpdateScenarios extends BaseTest {

    @Autowired
    protected ExtendedCcdHelper extendedCcdHelper;

    @Test
    public void testCcdCaseUpdate() {

        s2sAuthRequest()
            .relaxedHTTPSValidation()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .when()
            .get("/folders/functional-tests")
            .then()
            .statusCode(200);


        HearingRecordingDto reqBody = extendedCcdHelper.createRecordingSegment(
            "http://dm-store:8080/documents/e486435e-30e8-456c-9d4d-4adffcb50010",
            "functional-tests/hearing-recording-segment",
            ".mp4",
            12L,
            0
            );

        s2sAuthRequest()
            .relaxedHTTPSValidation()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .body(reqBody)
            .when()
            .post("/segments")
            .then()
            .statusCode(202);
    }
}
