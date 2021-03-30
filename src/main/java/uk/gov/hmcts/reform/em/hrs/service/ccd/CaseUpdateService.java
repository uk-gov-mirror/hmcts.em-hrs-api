package uk.gov.hmcts.reform.em.hrs.service.ccd;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;

import java.util.Optional;

@Service
public class CaseUpdateService {

    private final CcdDataStoreApiClient ccdDataStoreApiClient;

    public CaseUpdateService(final CcdDataStoreApiClient ccdDataStoreApiClient) {
        this.ccdDataStoreApiClient = ccdDataStoreApiClient;
    }

    public Long addRecordingToCase(final HearingRecordingDto recordingFile,
                                   Optional<Long> caseId) {
        if (caseId.isEmpty()) {
            return ccdDataStoreApiClient.createCase(recordingFile);
        } else {
            return ccdDataStoreApiClient.updateCaseData(caseId.get(), recordingFile);
        }
    }
}
