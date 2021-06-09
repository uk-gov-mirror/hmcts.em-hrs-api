package uk.gov.hmcts.reform.em.hrs.auditlog;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.hrs.domain.AuditActions;
import uk.gov.hmcts.reform.em.hrs.domain.AuditEntry;
import uk.gov.hmcts.reform.em.hrs.model.LogOnlyAuditEntry;

import java.util.Date;

class AuditLogFormatterTest {
    private final AuditLogFormatter alf = new AuditLogFormatter();

    @Test
    public void shouldFormatAuditEntryWithNoValuesPopulated() {
        AuditEntry entry = new LogOnlyAuditEntry();
        String result = alf.format(entry);
        System.out.println("Log Format=" + result);
        Assert.assertNotNull(result);
    }

    @Test
    public void shouldFormatAuditEntryWithAllValuesPopulated() {
        int numberOfFieldsInAuditEntryClass = 7;//including ID - here to catch if class is extended, but not formatter
        Date now = new Date(1);
        AuditEntry entry = new LogOnlyAuditEntry();

        entry.setIpAddress("ip");
        entry.setCaseId(1234567890123456789L);
        entry.setUsername("userName@hmcts.net.internal");
        entry.setEventDateTime(now);
        entry.setServiceName("SUT");
        entry.setAction(AuditActions.USER_DOWNLOAD_OK);

        String result = alf.format(entry);
        Assert.assertEquals(numberOfFieldsInAuditEntryClass, AuditEntry.class.getDeclaredFields().length);
        System.out.println("Log Format=" + result);
        Assert.assertEquals(
            "HRS-API dateTime:1970-01-01T01:00:00.001,"
                + "action:USER_DOWNLOAD_OK,"
                + "clientIp:ip,"
                + "service:SUT,"
                + "user:userName@hmcts.net.internal,"
                + "caseId:1234567890123456789",
            result
        );
    }

    @Test
    public void shouldTruncateMillisecondsFromDateWhenEqualToZero() {
        Date now = new Date(0);
        AuditEntry entry = new LogOnlyAuditEntry();

        entry.setEventDateTime(now);
        String result = alf.format(entry);
        System.out.println("Log Format=" + result);
        Assert.assertEquals(
            "HRS-API dateTime:1970-01-01T01:00:00",
            result
        );
    }


}
