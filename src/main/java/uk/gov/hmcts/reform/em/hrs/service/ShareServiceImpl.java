package uk.gov.hmcts.reform.em.hrs.service;


import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;
import javax.inject.Named;


@Named
public class ShareServiceImpl implements ShareService {

    private final NotificationService notificationService;

    private final HearingRecordingShareeService hearingRecordingShareeService;

    private final HearingRecordingSegmentService hearingRecordingSegmentService;

    @Value("${notify.emailTemplateId}")
    private String emailTemplateId;

    public ShareServiceImpl(NotificationService notificationService,
                            HearingRecordingSegmentService hearingRecordingSegmentService,
                            HearingRecordingShareeService hearingRecordingShareeService) {
        this.notificationService = notificationService;
        this.hearingRecordingSegmentService = hearingRecordingSegmentService;
        this.hearingRecordingShareeService = hearingRecordingShareeService;
    }

    @Override
    public void executeNotify(final UUID recordingId, final String emailAddress) {

//        String emailAddress = request.getParameter("emailAddress");
//

//
//            // Save the hearingRecordingSharee
//            HearingRecordingSharees hearingRecordingSharees = hearingRecordingShareesService
//                .createAndSaveEntry(emailAddress, hearingRecording);
//
//            // Get the Hearing Recording Segments associated with the Hearing Recording
//            List<HearingRecordingSegment> hearingRecordingSegmentList = hearingRecordingSegmentService.findByRecordingId(
//                hearingRecording.getId());
//
//
//            List<String> hearingRecordingSegmentUrls = hearingRecordingSegmentList.stream()
//                .map(hearingRecordingSegment -> ("https://SOMEPREFIXTBD" + hearingRecordingSegment.getFileName()))
//                .collect(Collectors.toList());
//
//
//            String jwt = request.getHeader("authorization");
//
//            notificationService.sendEmailNotification(
//                emailTemplateId,
//                jwt,
//                hearingRecordingSegmentUrls,
//                hearingRecording.getCaseReference(),
//                hearingRecording.getCreatedOn().toString(),
//                hearingRecordingSharees.getId()
//            );
//        } else {
//            throw new IllegalArgumentException();
//        }
    }
}
