package uk.gov.hmcts.reform.em.hrs.service.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.hrs.exception.EmailRecipientNotFoundException;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HearingReportEmailServiceTest {

    @Mock
    private EmailSender emailSender;

    @Mock
    private HearingReportService hearingReportService;

    private HearingReportEmailService hearingReportEmailService;

    @BeforeEach
    void setUp() {
        hearingReportEmailService = new HearingReportEmailService(
            emailSender,
            new String[]{"recipient@example.com"},
            "sender@example.com"
        );
    }

    @Test
    void should_throw_exception_when_recipients_are_null() {
        assertThrows(EmailRecipientNotFoundException.class, () -> {
            new HearingReportEmailService(
                emailSender,
                null,
                "sender@example.com"
            );
        });
    }

    @Test
    void should_throw_exception_when_recipients_are_empty() {
        assertThrows(EmailRecipientNotFoundException.class, () -> {
            new HearingReportEmailService(
                emailSender,
                new String[]{},
                "sender@example.com"
            );
        });
    }

    @Test
    void should_send_report_email_with_attachment() throws Exception {

        LocalDate reportDate = LocalDate.now().minusMonths(1);

        File reportFile = new File("report.csv");

        when(hearingReportService.createMonthlyReport(reportDate.getMonth(), reportDate.getYear()))
            .thenReturn(reportFile);
        when(hearingReportService.createEmailSubject(reportDate))
            .thenReturn("Subject: Monthly hearing report");
        when(hearingReportService.createBody(reportDate))
            .thenReturn("Email body");
        when(hearingReportService.getReportAttachmentName(reportDate))
            .thenReturn("attachment_report_name.csv");

        hearingReportEmailService.sendReport(reportDate, hearingReportService);

        verify(emailSender, times(1)).sendMessageWithAttachments(
            "Subject: Monthly hearing report",
            "Email body",
            "sender@example.com",
            new String[]{"recipient@example.com"},
            Map.of("attachment_report_name.csv", reportFile)
        );
    }
}
