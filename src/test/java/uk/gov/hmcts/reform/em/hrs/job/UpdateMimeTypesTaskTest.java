package uk.gov.hmcts.reform.em.hrs.job;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.em.hrs.dto.SegmentMimeTypeTaskDTO;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;
import uk.gov.hmcts.reform.em.hrs.service.MimeTypeUpdaterService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateMimeTypesTaskTest {

    @Mock
    private HearingRecordingSegmentRepository segmentRepository;
    @Mock
    private MimeTypeUpdaterService mimeTypeUpdaterService;

    @InjectMocks
    private UpdateMimeTypesTask updateMimeTypesTask;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(updateMimeTypesTask, "batchSize", 2);
        ReflectionTestUtils.setField(updateMimeTypesTask, "threadLimit", 3);
        ReflectionTestUtils.setField(updateMimeTypesTask, "processedSinceHours", 24);
    }

    @Test
    void shouldProcessSegmentsWhenSegmentsAreFound() {
        List<SegmentMimeTypeTaskDTO> segments = createSegments(3); // 3 segments, batch size 2 -> 2 batches
        when(segmentRepository.findSegmentsToProcess(any(LocalDateTime.class))).thenReturn(segments);

        updateMimeTypesTask.run();

        verify(segmentRepository, times(1)).findSegmentsToProcess(any(LocalDateTime.class));
        verify(mimeTypeUpdaterService, times(2)).updateMimeTypesForBatch(anyList());
    }

    @Test
    void shouldDoNothingWhenNoSegmentsAreFound() {
        when(segmentRepository.findSegmentsToProcess(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        updateMimeTypesTask.run();

        verify(segmentRepository, times(1)).findSegmentsToProcess(any(LocalDateTime.class));
        verify(mimeTypeUpdaterService, never()).updateMimeTypesForBatch(anyList());
    }

    @Test
    void shouldContinueProcessingWhenOneBatchFails() {
        List<SegmentMimeTypeTaskDTO> segments = createSegments(3); // 2 batches
        when(segmentRepository.findSegmentsToProcess(any(LocalDateTime.class))).thenReturn(segments);

        doThrow(new RuntimeException("Simulated DB error"))
            .doNothing()
            .when(mimeTypeUpdaterService).updateMimeTypesForBatch(anyList());

        updateMimeTypesTask.run();

        verify(segmentRepository, times(1)).findSegmentsToProcess(any(LocalDateTime.class));
        verify(mimeTypeUpdaterService, times(2)).updateMimeTypesForBatch(anyList());
    }

    @Test
    void shouldHandleTopLevelExceptionInRunMethod() {
        when(segmentRepository.findSegmentsToProcess(any(LocalDateTime.class)))
            .thenThrow(new RuntimeException("Simulated repository failure"));

        updateMimeTypesTask.run();

        verify(segmentRepository, times(1)).findSegmentsToProcess(any(LocalDateTime.class));
        verify(mimeTypeUpdaterService, never()).updateMimeTypesForBatch(anyList());
    }

    private List<SegmentMimeTypeTaskDTO> createSegments(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> new SegmentMimeTypeTaskDTO(UUID.randomUUID(), "file" + i))
            .toList();
    }
}
