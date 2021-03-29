package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.exception.HearingRecordingNotFoundException;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;
import uk.gov.hmcts.reform.em.hrs.util.Tuple2;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.CCD_CASE_ID;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.SEGMENTS_DOWNLOAD_LINKS;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.HEARING_RECORDING_WITH_SEGMENTS;

class HearingRecordingServiceImplTests {
    private final HearingRecordingRepository hearingRecordingRepository = mock(HearingRecordingRepository.class);
    private static final String EMAIL_DOMAIN = "https://SOMEPREFIXTBD";

    private final HearingRecordingServiceImpl underTest = new HearingRecordingServiceImpl(
        hearingRecordingRepository,
        EMAIL_DOMAIN
    );

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

        assertThat(result).isNotNull().satisfies(x -> {
            assertThat(x.getT1()).isNotNull().isInstanceOf(HearingRecording.class);
            assertThat(x.getT2()).isNotEmpty().hasSameElementsAs(SEGMENTS_DOWNLOAD_LINKS);
        });
        verify(hearingRecordingRepository, times(1)).findByCcdCaseId(anyLong());
    }

    //
    //    @Test
    //    public void testFindOneThatDoesNotExist() {
    //        when(this.hearingRecordingRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
    //        Optional<HearingRecording> hearingRecording = hearingRecordingService.findOne(TestUtil.RANDOM_UUID);
    //        assertFalse(hearingRecording.isPresent());
    //    }

}

