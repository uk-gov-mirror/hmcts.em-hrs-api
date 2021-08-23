package uk.gov.hmcts.reform.em.hrs;


import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.hrs.testutil.BlobUtil;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

public class IngestScenarios extends BaseTest {

    public static final int SEGMENT_COUNT = 12;

    @Autowired
    private BlobUtil testUtil;

    String caseRef;
    List<String> filenames = new ArrayList<String>();

    @Before
    public void setup() throws Exception {
        createFolderIfDoesNotExistInHrsDB(FOLDER);

        ZonedDateTime now = ZonedDateTime.now();
        String fileNamePrefixToNotDelete = JURISDICTION + "-" + LOCATION_CODE + "-" + CASEREF_PREFIX
            + datePartFormatter.format(now);

        //TODO CCD and postgres database ought to be cleaned out as well, by archiving the case -
        // however this functionality is not yet available
        testUtil.deleteFilesFromContainerNotMatchingPrefix(
            FOLDER,
            testUtil.cvpBlobContainerClient,
            fileNamePrefixToNotDelete
        );
        testUtil.deleteFilesFromContainerNotMatchingPrefix(
            FOLDER,
            testUtil.hrsBlobContainerClient,
            fileNamePrefixToNotDelete
        );

        caseRef = timebasedCaseRef();
        int cvpOriginalBlobCount = testUtil.getBlobCount(testUtil.cvpBlobContainerClient, FOLDER);

        for (int segmentIndex = 0; segmentIndex < SEGMENT_COUNT; segmentIndex++) {
            String filename = filename(caseRef, segmentIndex);
            filenames.add(filename);
            testUtil.uploadToCvpContainer(filename);
        }

        testUtil.checkIfUploadedToStore(FOLDER, cvpOriginalBlobCount, SEGMENT_COUNT, testUtil.cvpBlobContainerClient);
    }

    @Test
    public void shouldCreateHearingRecordingSegments() {
        int hrsOriginalBlobCount = testUtil.getBlobCount(testUtil.hrsBlobContainerClient, FOLDER);

        for (int segmentIndex = 0; segmentIndex < SEGMENT_COUNT; segmentIndex++) {
            postRecordingSegment(caseRef, segmentIndex)
                .then()
                .log().all()
                .statusCode(202);
        }

        testUtil.checkIfUploadedToStore(FOLDER, hrsOriginalBlobCount, SEGMENT_COUNT, testUtil.hrsBlobContainerClient);

        getFilenames(FOLDER)
            .assertThat().log().all()
            .statusCode(200)
            .body("folder-name", equalTo(FOLDER))
            .body("filenames", hasItem(filenames.get(SEGMENT_COUNT - 1)));

        LOGGER.info("*****************************");
        LOGGER.info("*****************************");
        LOGGER.info("*****************************");
        LOGGER.info("*****************************");
        LOGGER.info("*****************************");


        CaseDetails caseDetails = findCaseWithAutoRetry(caseRef);


        Map<String, Object> data = caseDetails.getData();
        LOGGER.info("data size: " + data.size()); //TODO when posting multisegment - this needs to match
        List recordingFiles = (ArrayList) data.get("recordingFiles");
        LOGGER.info("num recordings: " + recordingFiles.size());

    }

    @Test
    public void shouldNotCopyHearingRecordingSegmentWhenFileNameMalformed() throws Exception {
        caseRef = "I'm malformed now " + caseRef + " I'm malformed now";
        postRecordingSegment(caseRef, 0)
            .then()
            .log().all()
            .statusCode(202);

        TimeUnit.SECONDS.sleep(30);

        getFilenames(FOLDER)
            .assertThat().log().all()
            .statusCode(200)
            .body("folder-name", equalTo(FOLDER))
            .body("filenames", not(hasItem(filenames.get(0))));
    }
}
