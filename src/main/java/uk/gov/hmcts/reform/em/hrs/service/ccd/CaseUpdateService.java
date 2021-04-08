package uk.gov.hmcts.reform.em.hrs.service.ccd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;

import java.util.Optional;

@Service
public class CaseUpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseUpdateService.class);

    private final CcdDataStoreApiClient ccdDataStoreApiClient;

    public CaseUpdateService(final CcdDataStoreApiClient ccdDataStoreApiClient) {
        this.ccdDataStoreApiClient = ccdDataStoreApiClient;
    }

    public Long addRecordingToCase(final HearingRecordingDto recordingFile,
                                   Optional<Long> caseId) {
        if (caseId.isEmpty()) {
            LOGGER.info("creating a new case for recording: {}", recordingFile.getRecordingRef());
            return ccdDataStoreApiClient.createCase(recordingFile);
        } else {
            LOGGER.info("adding  recording ({}) to case({})",
                                      recordingFile.getRecordingRef(), caseId.get());
            return ccdDataStoreApiClient.updateCaseData(caseId.get(), recordingFile);
        }
    }
}
