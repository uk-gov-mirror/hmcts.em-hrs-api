package uk.gov.hmcts.reform.em.hrs.service.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {
    MonthlyHearingReportTask.class,
    MonthlyReportEmailSenderService.class,
    DateListConverter.class
})
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
public class MonthlyHearingReportTaskTest {

    @MockBean
    private MonthlyReportEmailSenderService monthlyReportEmailSenderService;

    @MockBean
    private MonthlyHearingReportService monthlyHearingReportService;

    @Autowired
    private MonthlyHearingReportTask monthlyHearingReportTask;


    @Test
    public void testRunSuccess() {
        doNothing().when(monthlyReportEmailSenderService)
            .sendReport(any(LocalDate.class), any(MonthlyHearingReportService.class));

        monthlyHearingReportTask.run();

        verify(monthlyReportEmailSenderService, times(3))
            .sendReport(any(LocalDate.class), any(MonthlyHearingReportService.class));
        verify(monthlyReportEmailSenderService)
            .sendReport(LocalDate.of(2024, 7, 1), monthlyHearingReportService);
        verify(monthlyReportEmailSenderService)
            .sendReport(LocalDate.of(2024, 8, 31), monthlyHearingReportService);
        verify(monthlyReportEmailSenderService)
            .sendReport(LocalDate.of(2024, 9, 15), monthlyHearingReportService);
    }

    @Test
    public void testRunWithException() {
        doThrow(new RuntimeException("Testing Exception"))
            .when(monthlyReportEmailSenderService)
            .sendReport(any(LocalDate.class), any(MonthlyHearingReportService.class));

        monthlyHearingReportTask.run();

        verify(monthlyReportEmailSenderService, times(3))
            .sendReport(any(LocalDate.class), any(MonthlyHearingReportService.class));
        verify(monthlyReportEmailSenderService)
            .sendReport(LocalDate.of(2024, 7, 1), monthlyHearingReportService);
        verify(monthlyReportEmailSenderService)
            .sendReport(LocalDate.of(2024, 8, 31), monthlyHearingReportService);
        verify(monthlyReportEmailSenderService)
            .sendReport(LocalDate.of(2024, 9, 15), monthlyHearingReportService);
    }

}
