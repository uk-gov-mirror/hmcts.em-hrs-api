package uk.gov.hmcts.reform.em.hrs.service.impl;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.HEARING_RECORDING_DTO;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.HEARING_RECORDING_WITH_SEGMENTS_1_2_and_3;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.RANDOM_UUID;

@ExtendWith(MockitoExtension.class)
class SegmentServiceImplTest {
    @Mock
    private HearingRecordingSegmentRepository segmentRepository;

    @InjectMocks
    private SegmentServiceImpl underTest;

    private final HearingRecordingDto dto = HEARING_RECORDING_DTO;
    private final HearingRecording recording = HEARING_RECORDING_WITH_SEGMENTS_1_2_and_3;

    @Test
    void testFindByRecordingId() {
        doReturn(Collections.emptyList()).when(segmentRepository).findByHearingRecordingId(RANDOM_UUID);

        final List<HearingRecordingSegment> segments = underTest.findByRecordingId(RANDOM_UUID);

        assertThat(segments).isEmpty();
        verify(segmentRepository, times(1)).findByHearingRecordingId(RANDOM_UUID);
    }

    @Nested
    class CreateAndSaveInitialSegment {

        @Test
        void shouldMapAndSaveSuccessfully() {
            underTest.createAndSaveInitialSegment(recording, dto);

            ArgumentCaptor<HearingRecordingSegment> segmentCaptor =
                ArgumentCaptor.forClass(HearingRecordingSegment.class);
            verify(segmentRepository).saveAndFlush(segmentCaptor.capture());

            assertSegmentMapping(segmentCaptor.getValue());
        }

        @Test
        void shouldPropagateConstraintViolationException() {
            doThrow(new ConstraintViolationException("test violation", null, null))
                .when(segmentRepository).saveAndFlush(any(HearingRecordingSegment.class));

            assertThrows(ConstraintViolationException.class, () -> underTest.createAndSaveInitialSegment(recording, dto));

            verify(segmentRepository).saveAndFlush(any(HearingRecordingSegment.class));
        }

        @Test
        void shouldPropagateOtherRuntimeExceptions() {
            doThrow(new RuntimeException("Generic DB error"))
                .when(segmentRepository).saveAndFlush(any(HearingRecordingSegment.class));

            assertThrows(RuntimeException.class, () -> underTest.createAndSaveInitialSegment(recording, dto));

            verify(segmentRepository).saveAndFlush(any(HearingRecordingSegment.class));
        }
    }

    @Nested
    class CreateAndSaveAdditionalSegment {

        @Test
        void shouldMapAndSaveSuccessfully() {
            underTest.createAndSaveAdditionalSegment(recording, dto);

            ArgumentCaptor<HearingRecordingSegment> segmentCaptor =
                ArgumentCaptor.forClass(HearingRecordingSegment.class);
            verify(segmentRepository).saveAndFlush(segmentCaptor.capture());

            assertSegmentMapping(segmentCaptor.getValue());
        }

        @Test
        void shouldHandleConstraintViolationExceptionGracefully() {
            doThrow(new ConstraintViolationException("test violation", null, null))
                .when(segmentRepository).saveAndFlush(any(HearingRecordingSegment.class));

            assertDoesNotThrow(() -> underTest.createAndSaveAdditionalSegment(recording, dto));

            verify(segmentRepository).saveAndFlush(any(HearingRecordingSegment.class));
        }

        @Test
        void shouldPropagateOtherRuntimeExceptions() {
            doThrow(new RuntimeException("Generic DB error"))
                .when(segmentRepository).saveAndFlush(any(HearingRecordingSegment.class));

            assertThrows(RuntimeException.class, () -> underTest.createAndSaveAdditionalSegment(recording, dto));

            verify(segmentRepository).saveAndFlush(any(HearingRecordingSegment.class));
        }
    }

    private void assertSegmentMapping(HearingRecordingSegment capturedSegment) {
        assertThat(capturedSegment.getFilename()).isEqualTo(dto.getFilename());
        assertThat(capturedSegment.getFileExtension()).isEqualTo(dto.getFilenameExtension());
        assertThat(capturedSegment.getFileSizeMb()).isEqualTo(dto.getFileSize());
        assertThat(capturedSegment.getFileMd5Checksum()).isEqualTo(dto.getCheckSum());
        assertThat(capturedSegment.getIngestionFileSourceUri()).isEqualTo(dto.getSourceBlobUrl());
        assertThat(capturedSegment.getRecordingSegment()).isEqualTo(dto.getSegment());
        assertThat(capturedSegment.getInterpreter()).isEqualTo(dto.getInterpreter());
        assertThat(capturedSegment.getHearingRecording()).isSameAs(recording);
    }
}
