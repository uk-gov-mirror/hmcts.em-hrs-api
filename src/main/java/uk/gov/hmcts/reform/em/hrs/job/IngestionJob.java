package uk.gov.hmcts.reform.em.hrs.job;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.IngestionService;
import uk.gov.hmcts.reform.em.hrs.util.IngestionQueue;

import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

@Component
public class IngestionJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(IngestionJob.class);

    @Autowired
    private IngestionQueue ingestionQueue;

    @Autowired
    private IngestionService ingestionService;

    // Required by Quartz
    public IngestionJob() {
    }

    IngestionJob(final IngestionQueue ingestionQueue, final IngestionService ingestionService) {
        this.ingestionQueue = ingestionQueue;
        this.ingestionService = ingestionService;
    }

    @Override
    protected void executeInternal(final JobExecutionContext context) {
        Optional.ofNullable(ingestionQueue.poll())
            .ifPresent(hrDto -> ingestGracefully(hrDto));
    }

    private void ingestGracefully(HearingRecordingDto hrDto) {
        try {
            ingestionService.ingest(hrDto);
        } catch (RejectedExecutionException re) {
            LOGGER.warn("Execution Rejected: {}", re);//likely to be timeouts with blobstore
        } catch (Exception e) {
            LOGGER.error("Unhandled Exception: {}", e);
        }
    }
}

