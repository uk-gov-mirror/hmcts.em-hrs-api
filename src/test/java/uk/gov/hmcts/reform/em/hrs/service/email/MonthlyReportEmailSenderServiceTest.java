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
class MonthlyReportEmailSenderServiceTest {

    @Mock
    private EmailSender emailSender;

    @Mock
    private MonthlyHearingReportService monthlyHearingReportService;

    private MonthlyReportEmailSenderService monthlyReportEmailSenderService;

    @BeforeEach
    void setUp() {
        monthlyReportEmailSenderService = new MonthlyReportEmailSenderService(
            emailSender,
            new String[]{"recipient@example.com"},
            "sender@example.com"
        );
    }

    @Test
    void should_throw_exception_when_recipients_are_null() {
        assertThrows(EmailRecipientNotFoundException.class, () -> {
            new MonthlyReportEmailSenderService(
                emailSender,
                null,
                "sender@example.com"
            );
        });
    }

    @Test
    void should_throw_exception_when_recipients_are_empty() {
        assertThrows(EmailRecipientNotFoundException.class, () -> {
            new MonthlyReportEmailSenderService(
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

        when(monthlyHearingReportService.createMonthlyReport(reportDate.getMonth(), reportDate.getYear()))
            .thenReturn(reportFile);
        when(monthlyHearingReportService.createEmailSubject(reportDate))
            .thenReturn("Subject: Monthly hearing report");
        when(monthlyHearingReportService.createBody(reportDate))
            .thenReturn("Email body");
        when(monthlyHearingReportService.getReportAttachmentName(reportDate))
            .thenReturn("attachment_report_name.csv");

        monthlyReportEmailSenderService.sendReport(reportDate, monthlyHearingReportService);

        verify(emailSender, times(1)).sendMessageWithAttachments(
            "Subject: Monthly hearing report",
            "Email body",
            "sender@example.com",
            new String[]{"recipient@example.com"},
            Map.of("attachment_report_name.csv", reportFile)
        );
    }
}
