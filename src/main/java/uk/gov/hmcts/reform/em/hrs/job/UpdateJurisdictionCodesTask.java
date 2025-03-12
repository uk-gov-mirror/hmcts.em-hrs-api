package uk.gov.hmcts.reform.em.hrs.job;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.exception.BlobCopyException;
import uk.gov.hmcts.reform.em.hrs.exception.BlobNotFoundException;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingService;
import uk.gov.hmcts.reform.em.hrs.service.ccd.CcdDataStoreApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

@Component
@ConditionalOnProperty(value = "scheduling.task.jurisdiction-codes.enabled")
public class UpdateJurisdictionCodesTask {

    private static final String TASK_NAME = "jurisdiction-codes";
    private static final Logger logger = getLogger(UpdateJurisdictionCodesTask.class);
    private final BlobContainerClient blobClient;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${scheduling.task.jurisdiction-codes.batch-size}")
    private int batchSize;

    private final CcdDataStoreApiClient ccdDataStoreApiClient;
    private final HearingRecordingService hearingRecordingService;

    public UpdateJurisdictionCodesTask(@Qualifier("jurisdictionCodesContainerClient") BlobContainerClient blobClient,
                                       NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                       CcdDataStoreApiClient ccdDataStoreApiClient,
                                       HearingRecordingService hearingRecordingService) {
        this.blobClient = blobClient;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.ccdDataStoreApiClient = ccdDataStoreApiClient;
        this.hearingRecordingService = hearingRecordingService;
    }


    @Scheduled(cron = "${scheduling.task.jurisdiction-codes.cron}", zone = "Europe/London")
    @SchedulerLock(name = TASK_NAME)
    public void run() {
        logger.info("Started {} job", TASK_NAME);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Optional<BlobClient> csvBlobClient = loadWorkbookBlobClient();
        if (csvBlobClient.isEmpty()) {
            throw new BlobNotFoundException("blobName", "jurisdictionWorkbook");
        }

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
             XSSFWorkbook workbook = loadWorkbook(csvBlobClient.get())) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            List<CompletableFuture<UpdateRecordingRecord>> futures = new ArrayList<>();
            Set<Long> ccdCaseIds = ConcurrentHashMap.newKeySet();
            int count = 0;

            Semaphore semaphore = new Semaphore(batchSize);

            for (Row row : sheet) {
                semaphore.acquire();  // Wait if the concurrency limit is hit
                logger.info("Processing row: {}", count++);

                UpdateRecordingRecord updateRecordingRecord = new UpdateRecordingRecord(
                        getStringCellValue(row.getCell(0)),
                        getStringCellValue(row.getCell(1)),
                        getStringCellValue(row.getCell(2))
                );

                Long ccdCaseId = hearingRecordingService.findCcdCaseIdByFilename(updateRecordingRecord.filename);

                if (!ccdCaseIds.add(ccdCaseId)) {
                    logger.info("Skipping duplicate ccd case id: {}", ccdCaseId);
                    continue;
                }
                logger.info("Processing ccd case id: {}", ccdCaseId);
                futures.add(
                        CompletableFuture.supplyAsync(() ->  {
                                    try {
                                        logger.info("Invoking updateCase for {}", updateRecordingRecord.filename);

                                        UpdateRecordingRecord updateRecordingRec = updateCase(updateRecordingRecord, ccdCaseId);
                                        if (Objects.isNull(updateRecordingRec)) {
                                            logger.warn("updateCase failed for {}", updateRecordingRecord.filename);
                                        }
                                        return updateRecordingRec;

                                    } catch (Exception ex) {
                                        logger.error("Error processing file: {}", ex.getMessage(), ex);
                                        return null;
                                    } finally {
                                        semaphore.release();  // Free up the slot
                                    }
                                },executorService)
                                .orTimeout(15, TimeUnit.SECONDS)  // Fail individual futures if they take too long
                                .exceptionally(ex -> {
                                    logger.error("Failed processing {}: {}", updateRecordingRecord.filename,
                                            ex.getMessage(), ex);
                                    return null;
                                })
                );

                if (futures.size() >= batchSize) {
                    processBatch(futures);
                }
            }

            // Process remaining records
            if (!futures.isEmpty()) {
                processBatch(futures);
            }

        } catch (IOException | InterruptedException e) {
            logger.error("Encountered error updating jurisdiction codes", e);
        }

        csvBlobClient.get().delete();
        stopWatch.stop();
        logger.info("Update job for jurisdiction codes took {} ms", stopWatch.getDuration().toMillis());
        logger.info("Finished {} job", TASK_NAME);
    }

    private UpdateRecordingRecord updateCase(UpdateRecordingRecord recordingRecord, Long ccdCaseId) {
        logger.info("UpdateCase with ccd case id: {}", ccdCaseId);
        String filename = recordingRecord.filename;
        if (Objects.isNull(ccdCaseId)) {
            logger.info("Failed to find ccd case id for filename: {}", filename);
            return null;
        }
        try {
            ccdDataStoreApiClient.updateCaseWithCodes(
                ccdCaseId, recordingRecord.jurisdictionCode, recordingRecord.serviceCode);
        } catch (Exception e) {
            logger.info("Failed to update case with jurisdiction and service codes for filename: {}",
                         filename, e);
            return null;
        }
        return recordingRecord;
    }

    private String getStringCellValue(Cell cell) {
        return Objects.isNull(cell) || cell.getStringCellValue().isBlank() ? null : cell.getStringCellValue();
    }

    private void batchUpdate(List<UpdateRecordingRecord> records) {
        for (int i = 0; i < records.size(); i += batchSize) {
            List<UpdateRecordingRecord> batchList = records.subList(i, Math.min(i + batchSize, records.size()));
            namedParameterJdbcTemplate.batchUpdate(
                "UPDATE hearing_recording "
                    + "SET jurisdiction_code = :jurisdictionCode, service_code = :serviceCode "
                    + "WHERE id = (SELECT hearing_recording_id "
                    + "FROM hearing_recording_segment "
                    + "WHERE filename = :filename)", SqlParameterSourceUtils.createBatch(batchList));
        }
    }

    private Optional<BlobClient> loadWorkbookBlobClient() {
        return blobClient.listBlobs()
            .stream()
            .map(blobItem -> blobClient.getBlobClient(blobItem.getName()))
            .findAny();
    }


    private XSSFWorkbook loadWorkbook(BlobClient client) throws IOException {
        File spreadsheetFile = null;
        try {
            spreadsheetFile = File.createTempFile("jurisdictionCodes", ".xslx");
            final String filename = spreadsheetFile.getAbsolutePath();

            Files.deleteIfExists(spreadsheetFile.toPath());
            client.downloadToFile(filename);

            try (InputStream stream = new FileInputStream(filename)) {
                return new XSSFWorkbook(stream);
            }
        } catch (IOException e) {
            throw new BlobCopyException(e.getMessage());
        } finally {
            if (Objects.nonNull(spreadsheetFile) && spreadsheetFile.exists()) {
                Files.deleteIfExists(spreadsheetFile.toPath());
            }
        }
    }

    private void processBatch(List<CompletableFuture<UpdateRecordingRecord>> futures) {
        logger.info("Waiting for {} futures to complete", futures.size());

        CompletableFuture<Void> batchFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            batchFuture.orTimeout(2, TimeUnit.MINUTES).join();  // Timeout the whole batch
        } catch (Exception e) {
            logger.error("Batch failed to complete within timeout", e);
        }

        List<UpdateRecordingRecord> completedRecords = futures.stream()
                .map(future -> {
                    try {
                        return future.join();  // join is safe here since the batchFuture already joined
                    } catch (Exception ex) {
                        logger.error("Future failed during batch join", ex);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        logger.info("Batch completed. {} records ready for batch update.", completedRecords.size());

        batchUpdate(completedRecords);

        futures.clear();
    }

    private record UpdateRecordingRecord(String filename, String jurisdictionCode, String serviceCode){}

}
