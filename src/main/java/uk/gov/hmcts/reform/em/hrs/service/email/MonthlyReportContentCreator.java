package uk.gov.hmcts.reform.em.hrs.service.email;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;

public interface MonthlyReportContentCreator {

    File createMonthlyReport(Month month, int year) throws IOException;

    String getSubjectPrefix();

    String getAttachmentPrefix();

    default String createEmailSubject(LocalDate reportDate) {
        return getSubjectPrefix() + reportDate;
    }

    default String createBody(LocalDate date) {
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

    default String getReportAttachmentName(LocalDate reportDate) {
        return getAttachmentPrefix() + reportDate.getMonth() + "-" + reportDate.getYear() + ".csv";
    }

    default LocalDateTime getStartOfMonth(Month month, int year) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        return firstDayOfMonth.atStartOfDay();
    }

    default LocalDateTime getEndOfMonth(Month month, int year) {
        LocalDate lastDayOfMonth = YearMonth.of(year, month).atEndOfMonth();
        return lastDayOfMonth.atTime(23, 59, 59);
    }
}
