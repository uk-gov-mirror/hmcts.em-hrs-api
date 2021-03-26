package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ShareServiceImplTests {


    @Mock
    private HearingRecordingShareeService hearingRecordingShareeService;

    @Mock
    private HearingRecordingSegmentService hearingRecordingSegmentService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ShareServiceImpl shareServiceImpl;

//    @Test
//    public void testExecuteUpdate() throws Exception {
//        HearingRecordingSharees hearingRecordingSharees = new HearingRecordingSharees();
//        UUID hearingRecordingShareesId = UUID.randomUUID();
//        hearingRecordingSharees.setId(hearingRecordingShareesId);
//
//        HearingRecording hearingRecording = new HearingRecording();
//        hearingRecording.setId(UUID.randomUUID());
//        hearingRecording.setCaseReference("test");
//        hearingRecording.setCreatedOn(LocalDateTime.now());
//
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addParameter("emailAddress", "test@tester.com");
//        request.addHeader("authorization", "XXX");
//
//        HearingRecordingSegment hearingRecordingSegment = new HearingRecordingSegment();
//        hearingRecordingSegment.setFileName("testFileName");
//        hearingRecordingSegment.setHearingRecording(hearingRecording);
//        List<HearingRecordingSegment> hearingRecordingSegmentList = new ArrayList<>();
//        hearingRecordingSegmentList.add(hearingRecordingSegment);
//
//        doReturn(hearingRecordingSharees).when(hearingRecordingShareesService)
//            .createAndSaveEntry("test@tester.com", hearingRecording);
//
//        doReturn(hearingRecordingSegmentList).when(hearingRecordingSegmentService)
//            .findByRecordingId(hearingRecording.getId());
//
//        shareServiceImpl.executeNotify(hearingRecording, request);
//
//
//        verify(hearingRecordingShareesService, times(1))
//            .createAndSaveEntry("test@tester.com", hearingRecording);
//
//        verify(hearingRecordingSegmentService, times(1))
//            .findByRecordingId(hearingRecording.getId());
//
//        List<String> docLink = new ArrayList<>();
//        docLink.add("https://SOMEPREFIXTBDtestFileName");
//
//        verify(notificationService, times(1))
//            .sendEmailNotification(null, "XXX",
//                                   docLink, "test",
//                                   hearingRecording.getCreatedOn().toString(),
//                                   hearingRecordingShareesId);
//    }
//
//    @Test
//    public void testExecuteUpdateWithIncorrectEmail() throws Exception {
//        HearingRecording hearingRecording = new HearingRecording();
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addParameter("emailAddress", "badEmail@tester,com");
//
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            shareServiceImpl.executeNotify(hearingRecording, request);
//        });
//    }
}
