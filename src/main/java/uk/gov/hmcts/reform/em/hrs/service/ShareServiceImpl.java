package uk.gov.hmcts.reform.em.hrs.service;

import reactor.util.function.Tuple2;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSharee;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ShareServiceImpl implements ShareService {
    private final HearingRecordingService hearingRecordingService;
    private final HearingRecordingShareeService hearingRecordingShareeService;
    private final SecurityService securityService;
    private final NotificationService notificationService;

    @Inject
    public ShareServiceImpl(final HearingRecordingService hearingRecordingService,
                            final HearingRecordingShareeService hearingRecordingShareeService,
                            final SecurityService securityService,
                            final NotificationService notificationService) {
        this.hearingRecordingService = hearingRecordingService;
        this.hearingRecordingShareeService = hearingRecordingShareeService;
        this.securityService = securityService;
        this.notificationService = notificationService;
    }

    @Override
    public void executeNotify(final Long ccdCaseId,
                              final String shareeEmailAddress,
                              final String authorisationToken) throws NotificationClientException {
        final Tuple2<HearingRecording, Set<String>> result = hearingRecordingService.getDownloadSegmentUris(ccdCaseId);
        final HearingRecording hearingRecording = result.getT1();

        final HearingRecordingSharee sharee = hearingRecordingShareeService.createAndSaveEntry(
            shareeEmailAddress,
            hearingRecording
        );

        final String sharerEmailAddress = securityService.getUserEmail(authorisationToken);

        notificationService.sendEmailNotification(
            hearingRecording.getCaseRef(),
            hearingRecording.getCreatedOn(),
            List.copyOf(result.getT2()),
            sharee.getId(),
            shareeEmailAddress,
            sharerEmailAddress
        );
    }
}
