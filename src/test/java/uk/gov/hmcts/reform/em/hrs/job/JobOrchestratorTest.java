package uk.gov.hmcts.reform.em.hrs.job;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JobOrchestratorTest {
    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private JobOrchestrator underTest;

    @Test
    void testShouldStartTheScheduler() throws Exception {
        doReturn(new Date()).when(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));

        underTest.start();

        verify(scheduler, times(1)).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void testShouldStopTheScheduler() throws Exception {
        doNothing().when(scheduler).shutdown(anyBoolean());

        underTest.stop();

        verify(scheduler, times(1)).shutdown(anyBoolean());
    }
}
