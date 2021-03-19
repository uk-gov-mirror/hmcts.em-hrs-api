package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HearingRecordingSegmentService {

    /**
     * Get all the segments of a HearingRecording
     *
     * @param id the id of the entity
     * @return the entity
     */
    List<HearingRecordingSegment> findByRecordingId(UUID id);
}
