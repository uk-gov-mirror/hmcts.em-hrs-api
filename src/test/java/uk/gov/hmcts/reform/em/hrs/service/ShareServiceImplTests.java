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
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.AUTHORIZATION_TOKEN;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.CASE_REFERENCE;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.CCD_CASE_ID;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.HEARING_RECORDING_SHAREE;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.HEARING_RECORDING_WITH_SEGMENTS;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.RECORDING_DATETIME;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.RECORDING_SEGMENT_DOWNLOAD_URLS;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.SEGMENTS_DOWNLOAD_LINKS;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.SHAREE_EMAIL_ADDRESS;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.SHAREE_ID;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.SHARER_EMAIL_ADDRESS;

@ExtendWith({MockitoExtension.class})
class ShareServiceImplTests {

    @Mock
    private HearingRecordingShareeService hearingRecordingShareeService;

    @Mock
    private HearingRecordingService hearingRecordingService;

    @Mock
    private SecurityService securityService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ShareServiceImpl underTest;

    @Test
    void testShouldSendNotificationSuccessfully() throws Exception {
        doReturn(new Tuple2<>(HEARING_RECORDING_WITH_SEGMENTS, SEGMENTS_DOWNLOAD_LINKS))
            .when(hearingRecordingService)
            .getDownloadSegmentUris(CCD_CASE_ID);
        doReturn(HEARING_RECORDING_SHAREE).when(hearingRecordingShareeService)
            .createAndSaveEntry(SHAREE_EMAIL_ADDRESS, HEARING_RECORDING_WITH_SEGMENTS);
        doReturn(SHARER_EMAIL_ADDRESS).when(securityService).getUserEmail(AUTHORIZATION_TOKEN);
        doNothing().when(notificationService)
            .sendEmailNotification(CASE_REFERENCE,
                                   RECORDING_DATETIME,
                                   RECORDING_SEGMENT_DOWNLOAD_URLS,
                                   SHAREE_ID,
                                   SHAREE_EMAIL_ADDRESS,
                                   SHARER_EMAIL_ADDRESS);

        underTest.executeNotify(CCD_CASE_ID, SHAREE_EMAIL_ADDRESS, AUTHORIZATION_TOKEN);

        verify(hearingRecordingService, times(1)).getDownloadSegmentUris(CCD_CASE_ID);
        verify(hearingRecordingShareeService, times(1))
            .createAndSaveEntry(SHAREE_EMAIL_ADDRESS, HEARING_RECORDING_WITH_SEGMENTS);
        verify(securityService, times(1)).getUserEmail(AUTHORIZATION_TOKEN);
        verify(notificationService, times(1))
            .sendEmailNotification(CASE_REFERENCE,
                                   RECORDING_DATETIME,
                                   RECORDING_SEGMENT_DOWNLOAD_URLS,
                                   SHAREE_ID,
                                   SHAREE_EMAIL_ADDRESS,
                                   SHARER_EMAIL_ADDRESS);
    }
}
