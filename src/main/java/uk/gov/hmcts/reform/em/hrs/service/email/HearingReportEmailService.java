package uk.gov.hmcts.reform.em.hrs.service.email;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.exception.EmailRecipientNotFoundException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

@Service
@Lazy
public class HearingReportEmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HearingReportEmailService.class);

    private final EmailSender emailSender;

    private final String[] recipients;

    private final String from;

    public HearingReportEmailService(
        EmailSender emailSender,
        @Value("${report.monthly-hearing.recipients}") String[] recipients,
        @Value("${report.from}") String from
    ) {
        this.emailSender = emailSender;
        if (ArrayUtils.isEmpty(recipients)) {
            throw new EmailRecipientNotFoundException("No recipients configured for reports");
        } else {
            this.recipients = Arrays.copyOf(recipients, recipients.length);
        }
        this.from = from;
    }

    public void sendReport(LocalDate reportDate, MonthlyReportContentCreator hearingReportService) {
        try {
            var reportFile = hearingReportService.createMonthlyReport(reportDate.getMonth(),reportDate.getYear());
            LOGGER.info("Report recipients: {}", this.recipients[0]);

            emailSender.sendMessageWithAttachments(
                hearingReportService.createEmailSubject(reportDate),
                hearingReportService.createBody(reportDate),
                from,
                recipients,
                Map.of(hearingReportService.getReportAttachmentName(reportDate), reportFile)
            );
        } catch (Exception ex) {
            LOGGER.error("Report sending failed ", ex);
        }
    }

}
