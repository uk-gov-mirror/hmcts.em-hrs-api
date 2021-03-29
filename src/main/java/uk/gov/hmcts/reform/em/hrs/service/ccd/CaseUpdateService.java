package uk.gov.hmcts.reform.em.hrs.service.ccd;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingService;

import java.util.Optional;

@Service
public class CaseUpdateService {

    private final HearingRecordingService hearingRecordingService;
    private final CcdDataStoreApiClient ccdDataStoreApiClient;

    public CaseUpdateService(final CcdDataStoreApiClient ccdDataStoreApiClient,
                             final HearingRecordingService hearingRecordingService) {
        this.ccdDataStoreApiClient = ccdDataStoreApiClient;
        this.hearingRecordingService = hearingRecordingService;
    }

    public Long addRecordingToCase(final HearingRecordingDto recordingFile) {
        Optional<Long> caseId = hearingRecordingService.checkIfCaseExists(recordingFile.getRecordingReference());
        if (caseId.isEmpty()) {
            caseId = Optional.of(ccdDataStoreApiClient.createCase(recordingFile).getId());
        } else {
            ccdDataStoreApiClient.updateCaseData(caseId.get().toString(), recordingFile);
        }
        return caseId.get();
    }
}
