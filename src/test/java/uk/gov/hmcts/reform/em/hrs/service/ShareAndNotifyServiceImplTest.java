package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.hrs.model.CaseDocument;
import uk.gov.hmcts.reform.em.hrs.model.CaseRecordingFile;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;

import java.util.*;

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
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.SHAREE_EMAIL_ADDRESS;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.SHAREE_ID;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.SHARER_EMAIL_ADDRESS;

@ExtendWith({MockitoExtension.class})
class ShareAndNotifyServiceImplTest {

    @Mock
    private ShareeService shareeService;

    @Mock
    private HearingRecordingRepository hearingRecordingRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ShareAndNotifyServiceImpl underTest;

    @Test
    void testShouldSendNotificationSuccessfully() throws Exception {
        doReturn(Optional.of(HEARING_RECORDING_WITH_SEGMENTS))
            .when(hearingRecordingRepository).findByCcdCaseId(CCD_CASE_ID);
        doReturn(HEARING_RECORDING_SHAREE)
            .when(shareeService).createAndSaveEntry(SHAREE_EMAIL_ADDRESS, HEARING_RECORDING_WITH_SEGMENTS);
        doReturn(SHARER_EMAIL_ADDRESS).when(securityService).getUserEmail(AUTHORIZATION_TOKEN);
        doNothing()
            .when(notificationService).sendEmailNotification(CASE_REFERENCE,
                                                             RECORDING_DATETIME,
                                                             List.copyOf(Collections.singleton("document-url")),
                                                             SHAREE_ID,
                                                             SHAREE_EMAIL_ADDRESS,
                                                             SHARER_EMAIL_ADDRESS);
        Set<CaseRecordingFile> segments = Collections.singleton(
            CaseRecordingFile.builder().recordingFile(CaseDocument.builder().binaryUrl("document-url").build()).build()
        );
        final CaseDetails caseDetails = CaseDetails.builder()
            .data(Map.of("recipientEmailAddress", SHAREE_EMAIL_ADDRESS,
                         "recordingFiles", segments))
            .id(CCD_CASE_ID)
            .build();

        underTest.shareAndNotify(CCD_CASE_ID, caseDetails.getData(), AUTHORIZATION_TOKEN);

        verify(hearingRecordingRepository, times(1)).findByCcdCaseId(CCD_CASE_ID);
        verify(shareeService, times(1))
            .createAndSaveEntry(SHAREE_EMAIL_ADDRESS, HEARING_RECORDING_WITH_SEGMENTS);
        verify(securityService, times(1)).getUserEmail(AUTHORIZATION_TOKEN);
        verify(notificationService, times(1))
            .sendEmailNotification(CASE_REFERENCE,
                                   RECORDING_DATETIME,
                                   List.copyOf(Collections.singleton("document-url")),
                                   SHAREE_ID,
                                   SHAREE_EMAIL_ADDRESS,
                                   SHARER_EMAIL_ADDRESS);
    }
}
