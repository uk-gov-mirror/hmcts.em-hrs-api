package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.util.function.Tuple2;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.exception.HearingRecordingNotFoundException;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.CCD_CASE_ID;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.HEARING_RECORDING;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.HEARING_RECORDING_WITH_SEGMENTS;

@ExtendWith(MockitoExtension.class)
class HearingRecordingServiceImplTests {
    @Mock
    private HearingRecordingRepository hearingRecordingRepository;

    @InjectMocks
    private HearingRecordingServiceImpl underTest;

    @Test
    void testShouldRaiseHearingRecordingNotFoundExceptionWhenHearingRecordingNotFound() {
        doReturn(Optional.empty()).when(hearingRecordingRepository).findByCcdCaseId(anyLong());

        assertThatExceptionOfType(HearingRecordingNotFoundException.class)
            .isThrownBy(() -> underTest.getDownloadSegmentUris(CCD_CASE_ID));

        verify(hearingRecordingRepository, times(1)).findByCcdCaseId(anyLong());
    }

    @Test
    void testShouldReturnSetOfDownloadSegmentUris() {
        doReturn(Optional.of(HEARING_RECORDING_WITH_SEGMENTS))
            .when(hearingRecordingRepository)
            .findByCcdCaseId(anyLong());

        final Tuple2<HearingRecording, Set<String>> result = underTest.getDownloadSegmentUris(CCD_CASE_ID);

        assertThat(result.getT1()).isNotNull().isInstanceOf(HearingRecording.class);
        assertThat(result.getT2()).isNotEmpty();//.hasSameElementsAs(SEGMENTS_DOWNLOAD_LINKS);
        verify(hearingRecordingRepository, times(1)).findByCcdCaseId(anyLong());
    }

    @Test
    void testShouldReturnEmptySetWhenNoSegmentsFound() {
        doReturn(Optional.of(HEARING_RECORDING))
            .when(hearingRecordingRepository)
            .findByCcdCaseId(anyLong());

        final Tuple2<HearingRecording, Set<String>> result = underTest.getDownloadSegmentUris(CCD_CASE_ID);

        assertThat(result.getT1()).isNotNull().isInstanceOf(HearingRecording.class);
        assertThat(result.getT2()).isEmpty();
        verify(hearingRecordingRepository, times(1)).findByCcdCaseId(anyLong());
    }

}

