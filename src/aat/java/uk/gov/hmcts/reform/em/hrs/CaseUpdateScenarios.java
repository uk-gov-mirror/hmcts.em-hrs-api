package uk.gov.hmcts.reform.em.hrs;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Ignore;
import org.junit.Test;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper.HRS_TESTER;

public class CaseUpdateScenarios extends BaseTest {

    private static final String FOLDER = "audiostream001";
    private static final String JURISDICTION_CODE = "FM";
    private static final String LOCATION_CODE = "0112";
    private static final String CVP_CASE_ID = "testfile200M";
    private static final String CASE_REF = getCaseRef(JURISDICTION_CODE, LOCATION_CODE, CVP_CASE_ID);

    @Test
    public void testCcdCaseUpdate() {
        getFilenames(FOLDER)
            .then()
            .statusCode(200);

        String recordingTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSS"));

        JsonNode reqBody = createRecordingSegment(FOLDER, JURISDICTION_CODE, LOCATION_CODE, CVP_CASE_ID,
                                                  recordingTime, 0, "mp4");

        postRecordingSegment(reqBody)
            .then()
            .statusCode(202);
    }

    @Ignore
    @Test
    public void testDocumentShare() {
        CaseDetails caseDetails = searchForCase(CASE_REF).orElseThrow();

        shareRecording("sharee@email.com", caseDetails)
            .then()
            .statusCode(200);
    }

    @Ignore
    @Test
    public void testRecordingDownload() {
        CaseDetails caseDetails = searchForCase(CASE_REF).orElseThrow();

        downloadRecording(HRS_TESTER, caseDetails.getData())
            .then()
            .statusCode(200);
    }
}
