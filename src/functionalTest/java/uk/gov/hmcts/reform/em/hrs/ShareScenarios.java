package uk.gov.hmcts.reform.em.hrs;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.hrs.testutil.BlobUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class ShareScenarios extends BaseTest {

    @Autowired
    private BlobUtil testUtil;

    private String caseRef;
    private String filename;
    private CaseDetails caseDetails;
    private int expectedFileSize;

    @Before
    public void setup() throws Exception {
        createFolderIfDoesNotExistInHrsDB(FOLDER);
        caseRef = timebasedCaseRef();
        filename = filename(caseRef,0);

        int cvpExistingBlobCount = testUtil.getBlobCount(testUtil.cvpBlobContainerClient, FOLDER);
        testUtil.uploadToCvpContainer(filename);
        testUtil.checkIfBlobUploadedToCvp(FOLDER, cvpExistingBlobCount);

        int hrsExistingBlobCount = testUtil.getBlobCount(testUtil.hrsBlobContainerClient, FOLDER);
        postRecordingSegment(caseRef, 0).then().statusCode(202);
        testUtil.checkIfUploadedToHrsStorage(FOLDER, hrsExistingBlobCount);

        caseDetails = findCaseWithAutoRetry(caseRef);

        expectedFileSize = testUtil.getTestFile().readAllBytes().length;
        assertThat(expectedFileSize, is(not(0)));
    }

    @Test
    public void shareeWithCaseworkerHrsRoleShouldBeAbleToDownloadRecordings() {
        final CallbackRequest callbackRequest = createCallbackRequest(caseDetails, CASEWORKER_HRS_USER);
        shareRecording(CASEWORKER_HRS_USER, CASE_WORKER_HRS_ROLE, callbackRequest)
            .then()
            .log().all()
            .assertThat()
            .statusCode(200);

        final byte[] downloadedFileBytes =
            downloadRecording(CASEWORKER_HRS_USER, CASE_WORKER_HRS_ROLE, caseDetails.getData())
                .then()
                .statusCode(200)
                .extract().response()
                .body().asByteArray();

        final int actualFileSize = downloadedFileBytes.length;
        assertThat(actualFileSize, is(expectedFileSize));
    }

    @Test
    public void shareeWithCaseworkerRoleShouldBeAbleToDownloadRecordings() {
        final CallbackRequest callbackRequest = createCallbackRequest(caseDetails, CASEWORKER_USER);
        shareRecording(CASEWORKER_USER, CASE_WORKER_ROLE, callbackRequest)
            .then()
            .log().all()
            .assertThat()
            .statusCode(200);

        final byte[] downloadedFileBytes =
            downloadRecording(CASEWORKER_USER, CASE_WORKER_ROLE, caseDetails.getData())
                .then()
                .statusCode(200)
                .extract().response()
                .body().asByteArray();

        final int actualFileSize = downloadedFileBytes.length;
        assertThat(actualFileSize, is(expectedFileSize));
    }

    @Test
    public void shareeWithCitizenRoleIsAbleToDownloadRecordings() {
        final CallbackRequest callbackRequest = createCallbackRequest(caseDetails, CITIZEN_USER);
        shareRecording(CITIZEN_USER, CITIZEN_ROLE, callbackRequest)
            .then()
            .log().all()
            .statusCode(200);

        final byte[] downloadedFileBytes = downloadRecording(CITIZEN_USER, CITIZEN_ROLE, caseDetails.getData())
            .then()
            .statusCode(200)
            .extract().response()
            .body().asByteArray();

        final int actualFileSize = downloadedFileBytes.length;
        assertThat(actualFileSize, is(expectedFileSize));
    }

    @Test
    public void shouldReturn400WhenShareHearingRecordingsToInvalidEmailAddress() {
        final CallbackRequest callbackRequest = createCallbackRequest(caseDetails, ERROR_SHAREE_EMAIL_ADDRESS);

        shareRecording(SHAREE_EMAIL_ADDRESS, CASE_WORKER_ROLE, callbackRequest)
            .then().log().all()
            .statusCode(400);
    }

    @Test
    public void shouldReturn404WhenShareHearingRecordingsToEmailAddressWithNonExistentCaseId() {
        caseDetails.setId(1l);
        final CallbackRequest callbackRequest = createCallbackRequest(caseDetails, SHAREE_EMAIL_ADDRESS);

        shareRecording(SHAREE_EMAIL_ADDRESS, CASE_WORKER_ROLE, callbackRequest)
            .then().log().all()
            .statusCode(404);

        caseDetails.setId(null);
    }
}
