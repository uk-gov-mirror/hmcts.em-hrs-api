package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HearingRecordingSegmentServiceImplTests {
    @Mock
    private HearingRecordingSegmentRepository hearingRecordingSegmentRepository;

    @InjectMocks
    private HearingRecordingSegmentServiceImpl hearingRecordingSegmentServiceImpl;

    @Test
    public void testFindByRecordingId() {

        // Search with a non-existent hearing recording id
        UUID recordingId = UUID.randomUUID();
        hearingRecordingSegmentServiceImpl.findByRecordingId(recordingId);

        // verify the hearingRecordingSegmentRepository is being called correctly
        verify(hearingRecordingSegmentRepository, times(1)).findByHearingRecordingId(recordingId);

    }



    @Test
    public void testCreateAndSaveEntity() {

        //  Create a HearingRecord
        UUID uuid = UUID.randomUUID();
        HearingRecordingSegment hearingRecordingSegment = new HearingRecordingSegment();
        hearingRecordingSegment.setId(uuid);
        hearingRecordingSegment.setCreatedBy("tester");


        // Save into the hearingRecordingSegmentService
        hearingRecordingSegmentServiceImpl.createAndSaveEntry(hearingRecordingSegment);

        // verify the hearingRecordingSegmentRepository is being called correctly
        verify(hearingRecordingSegmentRepository, times(1)).save(any(HearingRecordingSegment.class));
    }


}
