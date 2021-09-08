package uk.gov.hmcts.reform.em.hrs;


import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.hrs.testutil.BlobUtil;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

public class IngestScenarios extends BaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IngestScenarios.class);

    public static final int SEGMENT_COUNT = 2;//TODO set this 10 11 to test CCD validation changes

    @Autowired
    private BlobUtil testUtil;


    @Before
    public void setup() throws Exception {
        createFolderIfDoesNotExistInHrsDB(FOLDER);

    }


    @Test
    public void shouldCreateHearingRecordingSegments() throws Exception {
        String caseRef;
        Set<String> filenames = new HashSet<>();

        ZonedDateTime now = ZonedDateTime.now();
        String fileNamePrefixToNotDelete = JURISDICTION + "-" + LOCATION_CODE + "-" + CASEREF_PREFIX
            + datePartFormatter.format(now);

        //TODO CCD and postgres database ought to be cleaned out as well, by archiving the case -
        // however this functionality is not yet available

        LOGGER.info("************* CLEARING DOWN CVP STORE **********");

        testUtil.deleteFilesFromContainerNotMatchingPrefix(
            FOLDER,
            testUtil.cvpBlobContainerClient,
            fileNamePrefixToNotDelete
        );

        LOGGER.info("************* CLEARING DOWN HRS STORE **********");

        testUtil.deleteFilesFromContainerNotMatchingPrefix(
            FOLDER,
            testUtil.hrsBlobContainerClient,
            fileNamePrefixToNotDelete
        );

        caseRef = timebasedCaseRef();


        for (int segmentIndex = 0; segmentIndex < SEGMENT_COUNT; segmentIndex++) {
            String filename = filename(caseRef, segmentIndex);
            filenames.add(filename);
            testUtil.uploadToCvpContainer(filename);
        }

        LOGGER.info("************* CHECKING CVP HAS UPLOADED **********");
        testUtil.checkIfUploadedToStore(filenames, testUtil.cvpBlobContainerClient);
        LOGGER.info("************* Files loaded to cvp storage **********");


        for (int segmentIndex = 0; segmentIndex < SEGMENT_COUNT; segmentIndex++) {
            postRecordingSegment(caseRef, segmentIndex)
                .then()
                .log().all()
                .statusCode(202);
        }

        LOGGER.info("************* CHECKING HRS HAS COPIED **********");
        testUtil.checkIfUploadedToStore(filenames, testUtil.hrsBlobContainerClient);

        getFilenames(FOLDER)
            .assertThat().log().all()
            .statusCode(200)
            .body("folder-name", equalTo(FOLDER))
            .body("filenames", hasItems(filenames.toArray()));

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
}
