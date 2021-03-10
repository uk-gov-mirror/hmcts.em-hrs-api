package uk.gov.hmcts.reform.em.hrs.service.ccd;

import uk.gov.hmcts.reform.em.hrs.dto.RecordingFilenameDto;

public interface CaseDataService {

    Long addHRFileToCase(RecordingFilenameDto recordingFile);
}
