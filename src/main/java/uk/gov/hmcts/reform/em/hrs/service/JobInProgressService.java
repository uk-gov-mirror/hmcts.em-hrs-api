package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;

public interface JobInProgressService {
    void register(HearingRecordingDto hearingRecordingDto);
    void deRegister(HearingRecordingDto hearingRecordingDto);
}
