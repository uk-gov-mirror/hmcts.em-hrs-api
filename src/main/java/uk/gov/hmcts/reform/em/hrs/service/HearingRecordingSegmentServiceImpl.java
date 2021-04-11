package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;

import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@Transactional
public class HearingRecordingSegmentServiceImpl implements HearingRecordingSegmentService {

    private final HearingRecordingSegmentRepository segmentRepository;

    @Inject
    public HearingRecordingSegmentServiceImpl(final HearingRecordingSegmentRepository segmentRepository) {
        this.segmentRepository = segmentRepository;
    }

    @Override
    public List<HearingRecordingSegment> findByRecordingId(UUID id) {
        return segmentRepository.findByHearingRecordingId(id);
    }

}
