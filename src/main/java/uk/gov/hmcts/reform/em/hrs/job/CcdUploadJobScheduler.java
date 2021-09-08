package uk.gov.hmcts.reform.em.hrs.job;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class CcdUploadJobScheduler {
    private final Scheduler scheduler;
    private final int intervalInSeconds;

    @Inject
    public CcdUploadJobScheduler(final Scheduler scheduler, @Value("${hrs.ccdupload-interval-in-seconds}") final int intervalInSeconds) {
        this.scheduler = scheduler;
        this.intervalInSeconds = intervalInSeconds;
    }

    @PostConstruct
    public void start() throws SchedulerException {
        final String nameElement = "CCD-Upload";
        final String groupElement = "HRS-Ingestion-Jobs";
        final JobDetail jobDetail = JobBuilder.newJob(CcdUploadJob.class)
            .withIdentity(nameElement, groupElement)
            .withDescription("Ingests hearing recordings from CVP into HRS")
            .build();

        final Trigger trigger = TriggerBuilder.newTrigger()
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(intervalInSeconds)
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
