package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;

import java.util.Optional;
import java.util.UUID;

public interface HearingRecordingService {

    /**
     * Get the hearingRecording from the id
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<HearingRecording> findOne(UUID id);


    HearingRecording createAndSaveEntry(HearingRecording hearingRecording
    );
}
