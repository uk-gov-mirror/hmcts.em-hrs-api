package uk.gov.hmcts.reform.em.hrs.service.email;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MonthlyHearingReportTaskTest {


    @Test
    void should_call_summary_report_service() {
        // given
        var hearingReportEmailService = mock(MonthlyReportEmailSenderService.class);
        var hearingReportService = mock(MonthlyHearingReportService.class);
        var task = new MonthlyHearingReportTask(hearingReportEmailService, hearingReportService, new ArrayList<>());

        // when
        task.run();

        // then
        verify(hearingReportEmailService, times(1))
            .sendReport(LocalDate.now().minusMonths(1), hearingReportService);
    }

    @Test
    void should_call_summary_report_service_as_many_time_as_reportStartDate_count() {
        // given

        var reportStartDateList = List.of(
            LocalDate.now(),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(2)
        );
        var hearingReportEmailService = mock(MonthlyReportEmailSenderService.class);
        var hearingReportService = mock(MonthlyHearingReportService.class);

        doThrow(new RuntimeException("testing dummy error"))
            .when(hearingReportEmailService).sendReport(any(LocalDate.class), any(MonthlyHearingReportService.class));

        var task = new MonthlyHearingReportTask(hearingReportEmailService, hearingReportService, reportStartDateList);

        // when
        task.run();

        // then
        verify(hearingReportEmailService, times(3)).sendReport(any(), any());
        verify(hearingReportEmailService, times(1))
            .sendReport(LocalDate.now(), hearingReportService);
        verify(hearingReportEmailService, times(1))
            .sendReport(LocalDate.now().minusMonths(1), hearingReportService);
        verify(hearingReportEmailService, times(1))
            .sendReport(LocalDate.now().minusMonths(2), hearingReportService);
    }

}
