package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSharees;


public interface HearingRecordingShareesService {

    /**
     * Save a hearingRecordingSharee
     *
     * @param emailAddress and hearingRecording the entity to save
     * @return the persisted entity
     */
    HearingRecordingSharees createAndSaveEntry(String emailAddress, HearingRecording hearingRecording);
}
