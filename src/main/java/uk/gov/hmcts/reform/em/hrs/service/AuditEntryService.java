package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.domain.AuditActions;
import uk.gov.hmcts.reform.em.hrs.domain.AuditEntry;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingAuditEntry;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingAuditEntryRepository;

import java.util.Date;
import java.util.List;

@Service
public class AuditEntryService {

    @Autowired
    private HearingRecordingAuditEntryRepository hearingRecordingAuditEntryRepository;


    @Autowired
    private SecurityUtilService securityUtilService;

    public List<HearingRecordingAuditEntry> findHearingRecordingAudits(HearingRecording hearingRecording) {
        return hearingRecordingAuditEntryRepository.findByHearingRecordingOrderByEventDateTimeAsc(hearingRecording);
    }

    public HearingRecordingAuditEntry createAndSaveEntry(HearingRecording hearingRecording,
                                                         AuditActions action) {
        return createAndSaveEntry(
            hearingRecording,
            action,
            securityUtilService.getUserId(),
            securityUtilService.getCurrentlyAuthenticatedServiceName());
    }

    public HearingRecordingAuditEntry createAndSaveEntry(HearingRecording hearingRecording,
                                                         AuditActions action,
                                                         String username,
                                                         String serviceName) {
        HearingRecordingAuditEntry hearingRecordingAuditEntry = new HearingRecordingAuditEntry();
        populateCommonFields(hearingRecordingAuditEntry, action, username, serviceName);
        hearingRecordingAuditEntry.setHearingRecording(hearingRecording);
        hearingRecordingAuditEntryRepository.save(hearingRecordingAuditEntry);
        return hearingRecordingAuditEntry;
    }

    //TODO review if this commonCode is applicable in this project, as its only used once so far.
    private void populateCommonFields(AuditEntry auditEntry,
                                      AuditActions action,
                                      String username,
                                      String serviceName) {
        auditEntry.setAction(action);
        auditEntry.setUsername(username);
        auditEntry.setServiceName(serviceName);
        auditEntry.setEventDateTime(new Date());
    }


}
