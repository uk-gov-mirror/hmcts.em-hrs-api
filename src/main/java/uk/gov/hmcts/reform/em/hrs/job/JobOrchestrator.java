package uk.gov.hmcts.reform.em.hrs.job;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class JobOrchestrator {
    @Inject
    private Scheduler scheduler;

    @PostConstruct
    public void start() throws SchedulerException {
        final String nameElement = "CVP-Hearing-Recording";
        final String groupElement = "HRS-Ingestion-Jobs";
        final JobDetail jobDetail = JobBuilder.newJob(IngestionJob.class)
            .withIdentity(nameElement, groupElement)
            .withDescription("Ingests hearing recordings from CVP into HRS")
            .build();

        final int rate = 1; // TODO: consider making this configurable
        final Trigger trigger = TriggerBuilder.newTrigger()
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(rate)
                    .withMisfireHandlingInstructionIgnoreMisfires()
                    .repeatForever()
            ).build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    @PreDestroy
    public void stop() throws SchedulerException {
        scheduler.shutdown(true);
    }
}
