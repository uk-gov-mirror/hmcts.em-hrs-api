package uk.gov.hmcts.reform.em.hrs.service;

import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;

public abstract class MonthlyReportService {

    protected abstract Logger getLogger();

    protected abstract String getSubjectPrefix();

    protected abstract String getAttachmentPrefix();

    protected abstract File generateCsvReport(LocalDateTime startOfMonth, LocalDateTime endOfMonth) throws IOException;

    public File createMonthlyReport(Month month, int year) throws IOException {
        LocalDateTime startOfMonth = getStartOfMonth(month, year);
        LocalDateTime endOfMonth = getEndOfMonth(month, year);
        getLogger().info("Creating report for: {} - {}", startOfMonth, endOfMonth);
        return generateCsvReport(startOfMonth, endOfMonth);
    }

    public String createEmailSubject(LocalDate reportDate) {
        return getSubjectPrefix() + reportDate;
    }

    public String createBody(LocalDate date) {
        return """
            <html>
                <body>
                    <h1>%s %s/%d</h1>
                    <br>
                    <br><br>
                </body>
            </html>
            """.formatted(getSubjectPrefix(), date.getMonth(), date.getYear());
    }

    public String getReportAttachmentName(LocalDate reportDate) {
        return getAttachmentPrefix() + reportDate.getMonth() + "-" + reportDate.getYear() + ".csv";
    }

    private LocalDateTime getStartOfMonth(Month month, int year) {
        return LocalDate.of(year, month, 1).atStartOfDay();
    }

    private LocalDateTime getEndOfMonth(Month month, int year) {
        return YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);
    }
}
