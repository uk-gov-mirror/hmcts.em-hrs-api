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

    /*
    TODO note that CCD uses STD-OUT log forwarding rather than a DB audit table in the format of

    <DateTime> <Tag> Operation:<Operation Type> , Case_Id:<Case Id>[,<case Id>]..., IDAM_Id:<IDAM ID>,
     Invoking_service:<Invoking Service>, Endpoint_called:<Endpoint Called>,
      Operational_outcome:<Operational Outcome>[, Case_type:<Case Type>][, Jurisdiction:<Jurisdiction>]
      [, Event_selected:<Event Selected>][, IDAM_Id_of_target:<IDAM ID Of Target>]
       [,List_of_case_types:<List Of Case Types>][,Target_case_roles:<Target Case Roles>]
       [, X-Correlation-ID:<x-correlation-id>]

    https://tools.hmcts.net/confluence/display/RCCD/CCD+Case+Log+And+Audit+Design
    note that the page contains two variants of field naming - ie eventCategory + eventStatus vs operationType

   https://github.com/hmcts/ccd-data-store-api/tree/
   fe854a98ef57dfb2d63a05800de90a7618459f50/src/main/java/uk/gov/hmcts/ccd/auditlog

   Given that this service is mainly batch processing (except for user sharing & downloads), database makes more sense

   audit table field names are twinned with doc store rather than ccd terms at this point in time

     */







    @Autowired
    private HearingRecordingAuditEntryRepository hearingRecordingAuditEntryRepository;


    @Autowired
    private SecurityUtilService securityUtilService;

    public List<HearingRecordingAuditEntry> findStoredDocumentAudits(HearingRecording hearingRecording) {
        return hearingRecordingAuditEntryRepository.findByStoredDocumentOrderByRecordedDateTimeAsc(hearingRecording);
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
        auditEntry.setRecordedDateTime(new Date());
    }


}
