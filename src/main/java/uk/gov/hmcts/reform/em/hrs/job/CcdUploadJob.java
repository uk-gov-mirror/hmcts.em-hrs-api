package uk.gov.hmcts.reform.em.hrs.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.ccd.CcdUploadService;
import uk.gov.hmcts.reform.em.hrs.util.CcdUploadQueue;

import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@DisallowConcurrentExecution
public class CcdUploadJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CcdUploadJob.class);
    @Inject
    private CcdUploadQueue ccdUploadQueue;
    @Inject
    private CcdUploadService ccdUploadService;

    // Required by Quartz
    public CcdUploadJob() {
    }

    CcdUploadJob(final CcdUploadQueue ccdUploadQueue, final CcdUploadService ccdUploadService) {
        this.ccdUploadQueue = ccdUploadQueue;
        this.ccdUploadService = ccdUploadService;
    }

    @Override
    protected void executeInternal(final JobExecutionContext context) {
        Optional.ofNullable(ccdUploadQueue.poll())
            .ifPresent(hrDto -> uploadGracefully(hrDto));
    }

    private void uploadGracefully(HearingRecordingDto hrDto) {
        try {
            ccdUploadService.upload(hrDto);
        } catch (RejectedExecutionException re) {
            LOGGER.warn("Execution Rejected: {}", re);//likely to be timeouts with blobstore
        } catch (Exception e) {
            LOGGER.error("Unhandled Exception: {}", e);
        }
    }
}

