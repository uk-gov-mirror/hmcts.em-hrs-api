package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.hrs.util.Tuple2;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.CCD_CASE_ID;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.HEARING_RECORDING_WITH_SEGMENTS;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.RECIPIENT_EMAIL_ADDRESS;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.SEGMENTS_DOWNLOAD_LINKS;

@ExtendWith(MockitoExtension.class)
class ShareServiceImplTests {

    @Mock
    private HearingRecordingShareeService hearingRecordingShareeService;

    @Mock
    private HearingRecordingService hearingRecordingService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ShareServiceImpl underTest;

    @Test
    void testShouldSendNotificationSuccessfully() {
        doReturn(new Tuple2<>(HEARING_RECORDING_WITH_SEGMENTS, SEGMENTS_DOWNLOAD_LINKS))
            .when(hearingRecordingService)
            .getDownloadSegmentUris(CCD_CASE_ID);
        doNothing().when(hearingRecordingShareeService)
            .createAndSaveEntry(RECIPIENT_EMAIL_ADDRESS, HEARING_RECORDING_WITH_SEGMENTS);

        underTest.executeNotify(CCD_CASE_ID, RECIPIENT_EMAIL_ADDRESS, "XXX");

        verify(hearingRecordingService, times(1)).getDownloadSegmentUris(CCD_CASE_ID);
        verify(hearingRecordingShareeService, times(1))
            .createAndSaveEntry(RECIPIENT_EMAIL_ADDRESS, HEARING_RECORDING_WITH_SEGMENTS);
    }
}
