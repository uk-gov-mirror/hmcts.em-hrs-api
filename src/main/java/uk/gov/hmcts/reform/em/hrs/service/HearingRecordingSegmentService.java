package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;

import java.util.List;
import java.util.UUID;

public interface HearingRecordingSegmentService {

    List<HearingRecordingSegment> findByRecordingId(UUID id);
}
