package uk.gov.hmcts.reform.em.hrs;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.hrs.testutil.BlobTestUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class DownloadScenarios extends BaseTest {


    private String filename;
    @Autowired
    private BlobTestUtil testUtil;
    private String caseRef;
    private CaseDetails caseDetails;
    private int expectedFileSize;

    @Before
    public void setup() throws Exception {
        createFolderIfDoesNotExistInHrsDB(FOLDER);
        caseRef = timebasedCaseRef();
        filename = filename(caseRef, 0);

        int cvpBlobCount = testUtil.getBlobCount(testUtil.cvpBlobContainerClient,FOLDER);
        testUtil.uploadToCvpContainer(filename);
        testUtil.checkIfBlobUploadedToCvp(FOLDER, cvpBlobCount);


        int hrsBlobCount = testUtil.getBlobCount(testUtil.hrsBlobContainerClient,FOLDER);
        postRecordingSegment(caseRef, 0).then().statusCode(202);
        testUtil.checkIfUploadedToHrsStorage(FOLDER, hrsBlobCount);

        caseDetails = findCaseWithAutoRetry(caseRef);

        expectedFileSize = testUtil.getTestFile().readAllBytes().length;
        assertThat(expectedFileSize, is(not(0)));
    }

    @Test
    public void userWithCaseWorkerHrsRoleShouldBeAbleToDownloadHearingRecordings() {
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
    public void userWithCaseWorkerRoleShouldNotBeAbleToDownloadHearingRecordings() {
        downloadRecording(CASEWORKER_USER, CASE_WORKER_ROLE, caseDetails.getData())
            .then()
            .statusCode(403);
    }

    @Test
    public void userWithCitizenRoleShouldNotBeAbleToDownloadHearingRecordings() {
        downloadRecording(CITIZEN_USER, CITIZEN_ROLE, caseDetails.getData())
            .then()
            .statusCode(403);
    }
}
