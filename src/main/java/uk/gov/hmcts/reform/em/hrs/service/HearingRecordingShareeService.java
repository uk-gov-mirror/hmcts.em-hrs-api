package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSharee;


public interface HearingRecordingShareeService {

    /**
     * Save a hearingRecordingSharee.
     *
     * @param emailAddress and hearingRecording the entity to save.
     * @return the persisted entity.
     */
    HearingRecordingSharee createAndSaveEntry(String emailAddress, HearingRecording hearingRecording);
}
