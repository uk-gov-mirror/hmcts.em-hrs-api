package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.UUID;

@Named
@Service
@Transactional
public class HearingRecordingSegmentServiceImpl implements HearingRecordingSegmentService {
    private final HearingRecordingSegmentRepository hearingRecordingSegmentRepository;

    @Inject
    public HearingRecordingSegmentServiceImpl(final HearingRecordingSegmentRepository hearingRecordingSegmentRepository)
    {
        this.hearingRecordingSegmentRepository = hearingRecordingSegmentRepository;
    }

    public List<HearingRecordingSegment> findByRecordingId(UUID id) {
        List<HearingRecordingSegment> hearingRecordingSegmentsList = hearingRecordingSegmentRepository.findByRecordingId(id);
        return hearingRecordingSegmentsList;
    }
}
