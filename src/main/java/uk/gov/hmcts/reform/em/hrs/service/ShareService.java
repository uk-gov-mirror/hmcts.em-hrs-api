package uk.gov.hmcts.reform.em.hrs.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSharees;
import uk.gov.hmcts.reform.em.hrs.exception.JsonDocumentProcessingException;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;


@Service
public class ShareService {

    private final NotificationService notificationService;

    private final HearingRecordingShareesService hearingRecordingShareesService;

    private final HearingRecordingSegmentService hearingRecordingSegmentService;

    @Value("${notify.emailTemplateId}")
    private String emailTemplateId;

    public ShareService(NotificationService notificationService,
                        HearingRecordingSegmentService hearingRecordingSegmentService,
                        HearingRecordingShareesService hearingRecordingShareesService) {
        this.notificationService = notificationService;
        this.hearingRecordingSegmentService = hearingRecordingSegmentService;
        this.hearingRecordingShareesService = hearingRecordingShareesService;
    }


    public void executeNotify(HearingRecording hearingRecording, HttpServletRequest request)
        throws NotificationClientException, IOException, JsonDocumentProcessingException, IllegalArgumentException {

        // String emailAddress = notificationService.getUserEmail(jwt);
        String emailAddress = request.getParameter("emailAddress");

        if (Pattern.matches("^\\S+@\\S+\\.\\S+$", emailAddress)) {

            // Save the hearingRecordingSharee
            HearingRecordingSharees hearingRecordingSharees = hearingRecordingShareesService
                .createAndSaveEntry(emailAddress, hearingRecording);

            // Get the Hearing Recording Segments associated with the Hearing Recording
            List<HearingRecordingSegment> hearingRecordingSegmentList = hearingRecordingSegmentService.findByRecordingId(
                hearingRecording.getId());


            List<String> hearingRecordingSegmentUrls = hearingRecordingSegmentList.stream()
                .map(hearingRecordingSegment -> ("https://SOMEPREFIXTBD" + hearingRecordingSegment.getFileName()))
                .collect(Collectors.toList());


            String jwt = request.getHeader("authorization");

            notificationService.sendEmailNotification(
                emailTemplateId,
                jwt,
                hearingRecordingSegmentUrls,
                hearingRecording.getCaseReference(),
                hearingRecording.getCreatedOn().toString(),
                hearingRecordingSharees.getId()
            );
        } else {
            throw new IllegalArgumentException();
        }
    }
}
