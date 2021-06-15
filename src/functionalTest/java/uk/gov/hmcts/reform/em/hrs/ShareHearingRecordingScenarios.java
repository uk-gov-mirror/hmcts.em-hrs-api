package uk.gov.hmcts.reform.em.hrs;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.hrs.testutil.TestUtil;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class ShareHearingRecordingScenarios extends BaseTest {

    @Autowired
    private TestUtil testUtil;

    private String caseRef;
    private String filename;
    private CaseDetails caseDetails;
    private int expectedFileSize;

    @Before
    public void setup() throws Exception {
        createFolderIfDoesNotExistInHrsDB(FOLDER);
        caseRef = randomCaseRef();
        filename = filename(caseRef);
        testUtil.uploadToCvpContainer(filename);

        postRecordingSegment(caseRef).then().statusCode(202);
        TimeUnit.SECONDS.sleep(30);
        caseDetails = findCase(caseRef);

        expectedFileSize = testUtil.getTestFile().readAllBytes().length;
        assertThat(expectedFileSize, is(not(0)));
    }

    @After
    public void clear() {
        testUtil.deleteFileFromHrsContainer(FOLDER);
        testUtil.deleteFileFromCvpContainer(FOLDER);
    }

    @Test
    public void shareeWithCaseworkerRoleShouldBeAbleToDownloadRecordings() {
        final CallbackRequest callbackRequest = createCallbackRequest(caseDetails, CASEWORKER_USER);
        final Response shareRecordingResponse = shareRecording(CASEWORKER_USER, CASE_WORKER_ROLE, callbackRequest);

        shareRecordingResponse
            .then()
            .log().all()
            .assertThat()
            .statusCode(200);

        final byte[] downloadedFileBytes =
            downloadRecording(CASEWORKER_USER, CITIZEN_ROLE, caseDetails.getData())
                .then()
                .statusCode(200)
                .extract().response()
                .body().asByteArray();

        final int actualFileSize = downloadedFileBytes.length;

        assertThat(actualFileSize, is(not(0)));
        assertThat(actualFileSize, is(expectedFileSize));
    }

    @Test
    public void shareeWithCitizenRoleShouldNotBeAbleToDownloadRecordings() {
        final CallbackRequest callbackRequest = createCallbackRequest(caseDetails, CITIZEN_USER);
        final Response shareRecordingResponse = shareRecording(CITIZEN_USER, CITIZEN_ROLE, callbackRequest);

        shareRecordingResponse
            .then()
            .log().all()
            .assertThat()
            .statusCode(200);

        final byte[] downloadedFileBytes =
            downloadRecording(CITIZEN_USER, CITIZEN_ROLE, caseDetails.getData())
                .then()
                .statusCode(403) //FIXME citizen role should not download hearing recordings
                .extract().response()
                .body().asByteArray();

        final int actualFileSize = downloadedFileBytes.length;

        assertThat(actualFileSize, is(not(0)));
        assertThat(actualFileSize, is(expectedFileSize));
    }

    @Test
    public void shouldReturn400WhenShareHearingRecordingsToInvalidEmailAddress() {
        final CallbackRequest callbackRequest = createCallbackRequest(caseDetails, ERROR_SHAREE_EMAIL_ADDRESS);
        final Response shareRecordingResponse = shareRecording(SHAREE_EMAIL_ADDRESS, CASE_WORKER_ROLE, callbackRequest);

        shareRecordingResponse
            .then().log().all()
            .assertThat().statusCode(400);
    }

    @Test
    public void shouldReturn404WhenShareHearingRecordingsToEmailAddressWithNonExistentCaseId() {
        caseDetails.setId(RandomUtils.nextLong());
        final CallbackRequest callbackRequest = createCallbackRequest(caseDetails, SHAREE_EMAIL_ADDRESS);
        final Response shareRecordingResponse = shareRecording(SHAREE_EMAIL_ADDRESS, CASE_WORKER_ROLE, callbackRequest);

        shareRecordingResponse
            .then().log().all()
            .assertThat().statusCode(404);
    }
}
