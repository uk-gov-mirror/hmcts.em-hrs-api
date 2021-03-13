package uk.gov.hmcts.reform.em.hrs.service.ccd;

import uk.gov.hmcts.reform.em.hrs.dto.RecordingFilenameDto;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingServiceImpl;

import java.util.Optional;

public class CaseDataServiceImpl implements CaseDataService {

    private final CcdClient ccdClient;
    private final HearingRecordingServiceImpl hearingRecordingService;

    public CaseDataServiceImpl(CcdClient ccdClient, HearingRecordingServiceImpl hearingRecordingService) {
        this.ccdClient = ccdClient;
        this.hearingRecordingService = hearingRecordingService;
    }

    @Override
    public Long addHRFileToCase(RecordingFilenameDto recordingFile) {
        Optional<Long> caseId = hearingRecordingService.checkIfHRCaseAlredyCreated(recordingFile.getCaseRef());
        if (caseId.isEmpty()) {
            caseId = Optional.of(ccdClient.createHRCase(recordingFile));
        } else {
            ccdClient.updateHRCase(caseId.get().toString(), recordingFile);
        }
        return caseId.get();
    }
}
