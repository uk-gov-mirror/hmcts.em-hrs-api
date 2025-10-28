package uk.gov.hmcts.reform.em.hrs.job;

import com.microsoft.applicationinsights.core.dependencies.google.common.collect.Lists;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.dto.SegmentMimeTypeTaskDTO;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;
import uk.gov.hmcts.reform.em.hrs.service.MimeTypeUpdaterService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class UpdateMimeTypesTask implements Runnable {

    private static final Logger LOGGER = getLogger(UpdateMimeTypesTask.class);
    private static final String TASK_NAME = "mime-types";

    private final HearingRecordingSegmentRepository segmentRepository;
    private final MimeTypeUpdaterService mimeTypeUpdaterService;

    @Value("${scheduling.task.mime-types.batch-size}")
    private int batchSize;

    @Value("${scheduling.task.mime-types.thread-limit}")
    private int threadLimit;

    @Value("${scheduling.task.mime-types.processed-since-hours}")
    private int processedSinceHours;

    @Autowired
    public UpdateMimeTypesTask(
        HearingRecordingSegmentRepository segmentRepository,
        MimeTypeUpdaterService mimeTypeUpdaterService
    ) {
        this.segmentRepository = segmentRepository;
        this.mimeTypeUpdaterService = mimeTypeUpdaterService;
    }

    @Override
    public void run() {
        LOGGER.info("Started {} job.", TASK_NAME);
        final long startTime = System.currentTimeMillis();

        try {
            LOGGER.info("Finding segments to process from the last {} hours.", processedSinceHours);
            final List<SegmentMimeTypeTaskDTO> segments =
                segmentRepository.findSegmentsToProcess(LocalDateTime.now().minusHours(processedSinceHours));

            if (segments.isEmpty()) {
                LOGGER.info("No new segments found from the last {} hours to process.", processedSinceHours);
                return;
            }

            LOGGER.info("Found {} unprocessed segments from the last {} hours.", segments.size(), processedSinceHours);
            final List<List<SegmentMimeTypeTaskDTO>> batches = Lists.partition(
                segments,
                batchSize
            );
            LOGGER.info("Processing segments in {} batches.", batches.size());

            processBatchesInParallel(batches);

        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred during the {} job.", TASK_NAME, e);
        }

        final long endTime = System.currentTimeMillis();
        LOGGER.info("Finished {} job in {}ms.", TASK_NAME, (endTime - startTime));
    }

    /**
     * Manages the ExecutorService lifecycle and processes the given batches in parallel.
     *
     * @param batches A list of segment batches to process.
     */
    private void processBatchesInParallel(List<List<SegmentMimeTypeTaskDTO>> batches) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(threadLimit)) {

            batches.forEach(batch -> executorService.submit(() -> processBatch(batch)));

            executorService.shutdown();

            if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
                LOGGER.warn("Job thread pool termination timeout exceeded. Forcing shutdown...");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Job thread pool was interrupted while awaiting termination.", e);
            Thread.currentThread().interrupt();
        }
    }

    private void processBatch(final List<SegmentMimeTypeTaskDTO> batch) {
        try {
            mimeTypeUpdaterService.updateMimeTypesForBatch(batch);
        } catch (Exception e) {
            LOGGER.error(
                "Failed to process batch of {} segments. Transaction rolled back. Reason: {}",
                batch.size(),
                e.getMessage()
            );
        }
    }
}
