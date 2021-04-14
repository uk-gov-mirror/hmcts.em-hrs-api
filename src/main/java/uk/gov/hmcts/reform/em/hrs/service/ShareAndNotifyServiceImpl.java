package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSharee;
import uk.gov.hmcts.reform.em.hrs.exception.GovNotifyErrorException;
import uk.gov.hmcts.reform.em.hrs.exception.HearingRecordingNotFoundException;
import uk.gov.hmcts.reform.em.hrs.exception.ValidationErrorException;
import uk.gov.hmcts.reform.em.hrs.model.CaseRecordingFile;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;
import uk.gov.hmcts.reform.em.hrs.util.EmailValidator;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShareAndNotifyServiceImpl implements ShareAndNotifyService {
    private final HearingRecordingRepository hearingRecordingRepository;
    private final ShareeService shareeService;
    private final SecurityService securityService;
    private final NotificationService notificationService;

    @Autowired
    public ShareAndNotifyServiceImpl(final HearingRecordingRepository hearingRecordingRepository,
                                     final ShareeService shareeService,
                                     final SecurityService securityService,
                                     final NotificationService notificationService) {
        this.hearingRecordingRepository = hearingRecordingRepository;
        this.shareeService = shareeService;
        this.securityService = securityService;
        this.notificationService = notificationService;
    }

    @Override
    public void shareAndNotify(final Long caseId, Map<String, Object> caseData, final String authorisationToken) {

        String shareeEmailAddress = caseData.get("recipientEmailAddress").toString();

        if (!EmailValidator.isValid(shareeEmailAddress)) {
            throw new ValidationErrorException("Invalid recipient email address");
        }

        @SuppressWarnings("unchecked")
        List<String> segmentUrls = ((Set<CaseRecordingFile>) caseData.get("recordingFiles")).stream()
            .map(segment -> segment.getRecordingFile())
            .map(file -> file.getBinaryUrl())
            .collect(Collectors.toList());

        final HearingRecording hearingRecording = hearingRecordingRepository.findByCcdCaseId(caseId)
            .orElseThrow(() -> new HearingRecordingNotFoundException(caseId));

        final HearingRecordingSharee sharee = shareeService.createAndSaveEntry(
            shareeEmailAddress,
            hearingRecording
        );

        final String sharerEmailAddress = securityService.getUserEmail(authorisationToken);

        try {
            notificationService.sendEmailNotification(
                hearingRecording.getCaseRef(),
                hearingRecording.getCreatedOn(),
                List.copyOf(segmentUrls),
                sharee.getId(),
                shareeEmailAddress,
                sharerEmailAddress
            );
        } catch (NotificationClientException e) {
            throw new GovNotifyErrorException(e);
        }
    }
}
