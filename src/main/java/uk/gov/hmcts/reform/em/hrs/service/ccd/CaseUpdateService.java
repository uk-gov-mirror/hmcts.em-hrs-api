package uk.gov.hmcts.reform.em.hrs.service.ccd;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingServiceImpl;

import java.util.Optional;

@Service
public class CaseUpdateService {

    private final HearingRecordingServiceImpl hearingRecordingService;
    private final CcdDataStoreApiClient ccdDataStoreApiClient;

    public CaseUpdateService(CcdDataStoreApiClient ccdDataStoreApiClient,
                             HearingRecordingServiceImpl hearingRecordingService) {
        this.ccdDataStoreApiClient = ccdDataStoreApiClient;
        this.hearingRecordingService = hearingRecordingService;
    }

    public Long addHRFileToCase(HearingRecordingDto recordingFile) {
        Optional<Long> caseId = hearingRecordingService
            .checkIfHRCaseAlredyCreated(recordingFile.getRecordingReference());
        if (caseId.isEmpty()) {
            caseId = Optional.of(ccdDataStoreApiClient.createHRCase(recordingFile).getId());
        } else {
            ccdDataStoreApiClient.updateHRCaseData(caseId.get().toString(), recordingFile);
        }
        return caseId.get();
    }
}
