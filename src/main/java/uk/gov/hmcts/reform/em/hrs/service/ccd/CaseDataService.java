package uk.gov.hmcts.reform.em.hrs.service.ccd;

import uk.gov.hmcts.reform.em.hrs.dto.RecordingFilenameDto;

import java.io.IOException;

public interface CaseDataService {

    Long addHRFileToCase(RecordingFilenameDto recordingFile) throws IOException;
}
