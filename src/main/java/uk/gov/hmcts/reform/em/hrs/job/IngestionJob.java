package uk.gov.hmcts.reform.em.hrs.job;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.IngestionService;
import uk.gov.hmcts.reform.em.hrs.service.JobInProgressService;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class IngestionJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(IngestionJob.class);
    @Inject
    @Qualifier("ingestionQueue")
    private LinkedBlockingQueue<HearingRecordingDto> ingestionQueue;

    @Inject
    private JobInProgressService jobInProgressService;

    @Inject
    private IngestionService ingestionService;

    @Inject
    @Qualifier("ccdUploadQueue")
    private LinkedBlockingQueue<HearingRecordingDto> ccdUploadQueue;

    // Required by Quartz
    public IngestionJob() {
    }

    //POJO Constructor for mocked tests without dependency injection
    IngestionJob(final LinkedBlockingQueue<HearingRecordingDto> ingestionQueue,
                 final IngestionService ingestionService, final JobInProgressService jobInProgressService,
                 LinkedBlockingQueue<HearingRecordingDto> ccdUploadQueue) {
        this.ingestionQueue = ingestionQueue;
        this.ingestionService = ingestionService;
        this.jobInProgressService = jobInProgressService;
        this.ccdUploadQueue = ccdUploadQueue;
    }

    @Override
    protected void executeInternal(final JobExecutionContext context) {
        Optional.ofNullable(ingestionQueue.poll())
            .ifPresent(hrDto -> ingestGracefully(hrDto));
    }

    private void ingestGracefully(HearingRecordingDto hrDto) {
        jobInProgressService.register(hrDto);

        try {
            ingestionService.ingest(hrDto);
            boolean accepted = ccdUploadQueue.offer(hrDto);
            if (!accepted) {
                LOGGER.warn("CCD Upload Queue Full. Not uploading file: {} ", hrDto.getFilename());
                jobInProgressService.deRegister(hrDto);
            }
        } catch (RejectedExecutionException re) {
            LOGGER.warn("Execution Rejected: {}", re);//likely to be timeouts with blobstore
            jobInProgressService.deRegister(hrDto);//remove from job in progress
        } catch (Exception e) {
            LOGGER.error("Unhandled Exception: {}", e);
            jobInProgressService.deRegister(hrDto);
        }
    }
}

