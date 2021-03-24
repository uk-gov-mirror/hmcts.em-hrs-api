package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSharees;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class ShareServiceTests {


    @Mock
    private HearingRecordingShareesService hearingRecordingShareesService;

    @Mock
    private HearingRecordingSegmentService hearingRecordingSegmentService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ShareService shareService;

    @Test
    public void testExecuteUpdate() throws Exception {
//        when(Mockito.any(HttpServletRequest.class).getParameter("emailAddress"))
//            .thenReturn("test@tester.com");
        HearingRecordingSharees hearingRecordingSharees = new HearingRecordingSharees();
        UUID hearingRecordingShareesId = UUID.randomUUID();
        hearingRecordingSharees.setId(hearingRecordingShareesId);

        HearingRecording hearingRecording = new HearingRecording();
        hearingRecording.setId(UUID.randomUUID());
        hearingRecording.setCaseReference("test");
        hearingRecording.setCreatedOn(LocalDateTime.now());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("emailAddress", "test@tester.com");
        request.addHeader("authorization", "XXX");

        HearingRecordingSegment hearingRecordingSegment = new HearingRecordingSegment();
        hearingRecordingSegment.setFileName("testFileName");
//        UUID hearingRecordingSegmentId = UUID.randomUUID();
        hearingRecordingSegment.setHearingRecording(hearingRecording);
        List<HearingRecordingSegment> hearingRecordingSegmentList = new ArrayList<>();
        hearingRecordingSegmentList.add(hearingRecordingSegment);

        doReturn(hearingRecordingSharees).when(hearingRecordingShareesService)
            .createAndSaveEntry("test@tester.com", hearingRecording);

        doReturn(hearingRecordingSegmentList).when(hearingRecordingSegmentService)
            .findByRecordingId(hearingRecording.getId());

        shareService.executeNotify(hearingRecording, request);


        verify(hearingRecordingShareesService, times(1))
            .createAndSaveEntry("test@tester.com", hearingRecording);

        verify(hearingRecordingSegmentService, times(1))
            .findByRecordingId(hearingRecording.getId());

        List<String> docLink = new ArrayList<>();
        docLink.add("https://SOMEPREFIXTBDtestFileName");

        verify(notificationService, times(1))
            .sendEmailNotification(null, "XXX",
                                   docLink, "test",
                                   hearingRecording.getCreatedOn().toString(),
                                   hearingRecordingShareesId);
    }
}
