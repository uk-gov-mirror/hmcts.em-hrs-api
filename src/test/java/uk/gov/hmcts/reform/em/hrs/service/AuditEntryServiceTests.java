package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil;
import uk.gov.hmcts.reform.em.hrs.domain.AuditActions;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingAuditEntry;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingAuditEntryRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuditEntryServiceTests {

    @Mock
    private HearingRecordingAuditEntryRepository hearingRecordingAuditEntryRepository
        ;


    @Mock
    private SecurityUtilService securityUtilService;

    @InjectMocks
    AuditEntryService auditEntryService;

    @Test
    public void testCreateAndSaveEntryForHearingRecording() {

        when(securityUtilService.getUserId()).thenReturn("x");
        when(securityUtilService.getCurrentlyAuthenticatedServiceName()).thenReturn("s");

        HearingRecordingAuditEntry entry = auditEntryService.createAndSaveEntry(new HearingRecording(),
                                                                                AuditActions.DATA_LIFECYCLE_CREATED);

        Assert.assertEquals("x", entry.getUsername());
        Assert.assertEquals("s", entry.getServiceName());

        verify(hearingRecordingAuditEntryRepository, times(1)).save(any(HearingRecordingAuditEntry.class));
    }


    @Test
    public void testFindHearingRecordingAudits() {
        when(hearingRecordingAuditEntryRepository
                .findByStoredDocumentOrderByRecordedDateTimeAsc(TestUtil.HEARING_RECORDING))
                .thenReturn(Stream.of(new HearingRecordingAuditEntry()).collect(Collectors.toList()));
        List<HearingRecordingAuditEntry> entries = auditEntryService.findStoredDocumentAudits(TestUtil.HEARING_RECORDING);
        Assert.assertEquals(1, entries.size());
    }


}
