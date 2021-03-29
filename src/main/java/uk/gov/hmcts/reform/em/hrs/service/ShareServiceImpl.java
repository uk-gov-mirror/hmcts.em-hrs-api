package uk.gov.hmcts.reform.em.hrs.service;


import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.util.Tuple2;

import java.util.Set;
import javax.inject.Named;


@Named
public class ShareServiceImpl implements ShareService {

    private final HearingRecordingService hearingRecordingService;
    private final HearingRecordingShareeService hearingRecordingShareeService;
    private final NotificationService notificationService;

    public ShareServiceImpl(HearingRecordingService hearingRecordingService,
                            HearingRecordingShareeService hearingRecordingShareeService,
                            NotificationService notificationService) {
        this.hearingRecordingService = hearingRecordingService;
        this.hearingRecordingShareeService = hearingRecordingShareeService;
        this.notificationService = notificationService;
    }

    @Override
    public void executeNotify(final Long ccdCaseId, final String recipientEmailAddress, final String authorizationJwt) {
        final Tuple2<HearingRecording, Set<String>> result = hearingRecordingService.getDownloadSegmentUris(ccdCaseId);

        hearingRecordingShareeService.createAndSaveEntry(recipientEmailAddress, result.getT1());

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
