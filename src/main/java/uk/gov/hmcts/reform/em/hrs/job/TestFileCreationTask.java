package uk.gov.hmcts.reform.em.hrs.job;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.config.TTLMapperConfig;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.dto.HearingSource;
import uk.gov.hmcts.reform.em.hrs.exception.DatabaseStorageException;
import uk.gov.hmcts.reform.em.hrs.service.FolderService;
import uk.gov.hmcts.reform.em.hrs.service.ccd.CcdUploadService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@ConditionalOnProperty(name = "scheduling.task.test-file-creation.enabled")
public class TestFileCreationTask {

    @Value("${scheduling.task.test-file-creation.test-cases}")
    private int testCasesToCreate;

    @Value("${scheduling.task.test-file-creation.max-concurrent-uploads}")
    private int maxConcurrentUploads;

    @Value("${base.url}")
    private String baseUrl;

    private static final String[] LOCATION_CODES = {"0372", "0266"};
    private static final String TASK_NAME = "test-file-upload";
    private static final Logger LOGGER = LoggerFactory.getLogger(TestFileCreationTask.class);

    private final TTLMapperConfig ttlMapperConfig;
    private final BlobContainerClient hrsCvpBlobContainerClient;
    private final CcdUploadService ccdUploadService;
    private final FolderService folderService;
    private final String folderName = "audiostream6540/";

    @Autowired
    public TestFileCreationTask(TTLMapperConfig ttlMapperConfig,
                                @Qualifier("hrsCvpBlobContainerClient") BlobContainerClient hrsCvpBlobContainerClient,
                                CcdUploadService ccdUploadService, FolderService folderService) {
        this.ttlMapperConfig = ttlMapperConfig;
        this.hrsCvpBlobContainerClient = hrsCvpBlobContainerClient;
        this.ccdUploadService = ccdUploadService;
        this.folderService = folderService;
    }

    @Scheduled(cron = "${scheduling.task.test-file-creation.schedule}")
    @SchedulerLock(name = TASK_NAME)
    public void run() {
        LOGGER.info("Started {} job", TASK_NAME);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        File inputFile = loadInputFile();

        List<HearingRecordingDto> recordingDtos = new ArrayList<>();

        try {
            folderService.getFolderByName(folderName);
        } catch (DatabaseStorageException e) {
            LOGGER.info("Folder {} does not exist, creating", folderName);
            folderService.getStoredFiles(folderName);
        }

        int totalFiles = 0;
        for (int i = 0; i < testCasesToCreate; i++) {
            String caseRef = "Test" + i * 1000;
            boolean includeLocationCode = i % 3 == 1;
            long fileSize = inputFile.length();
            HearingRecordingDto segment0Dto = createRecordingDto(caseRef, includeLocationCode, fileSize, 0);
            recordingDtos.add(segment0Dto);
            totalFiles++;

            // Create an additional file with segment 1 for 10% of the files
            if (i % 10 == 0) {
                HearingRecordingDto segment1Dto = copyWithNewSegment(segment0Dto, 1);
                recordingDtos.add(segment1Dto);
                totalFiles++;
            }

            if (recordingDtos.size() >= maxConcurrentUploads) {
                uploadFiles(inputFile, recordingDtos);
                recordingDtos.clear();
            }
        }

        if (!recordingDtos.isEmpty()) {
            uploadFiles(inputFile, recordingDtos);
        }

        stopWatch.stop();
        LOGGER.info("Test file creation job took {} ms to create {} files and {} cases",
                stopWatch.getDuration().toMillis(), totalFiles, testCasesToCreate);
        LOGGER.info("Finished {} job", TASK_NAME);
    }

    private HearingRecordingDto createRecordingDto(String caseRef, boolean includeLocationCode,
                                                   long fileSize, int segment) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSS");
        String datePart = now.format(formatter);
        String serviceCode = getRandomElement(ttlMapperConfig.getTtlServiceMap().keySet().toArray(new String[0]));
        String locationCodePart = includeLocationCode ? "-" + getRandomElement(LOCATION_CODES) : "";
        String baseFileName = String.format("%s%s-%s_%s-UTC", serviceCode, locationCodePart, caseRef, datePart);
        String filename = folderName + addSegment(baseFileName, segment);

        return HearingRecordingDto.builder()
            .caseRef(caseRef)
            .recordingSource(HearingSource.CVP)
            .filename(filename)
            .serviceCode(serviceCode)
            .courtLocationCode(getRandomElement(LOCATION_CODES))
            .segment(segment)
            .folder(folderName)
            .hearingRoomRef("123")
            .jurisdictionCode(null)
            .recordingRef(baseFileName)
            .sourceBlobUrl(null)
            .urlDomain(baseUrl)
            .filenameExtension("mp4")
            .fileSize(fileSize)
            .checkSum("dummyCheckSum")
            .interpreter("dummyInterpreter")
            .recordingDateTime(now)
            .build();
    }

    private HearingRecordingDto copyWithNewSegment(HearingRecordingDto originalDto, int newSegment) {
        String baseFileName = originalDto.getFilename().substring(0, originalDto.getFilename().lastIndexOf('_'));
        String newFilename = addSegment(baseFileName, newSegment);

        return HearingRecordingDto.builder()
            .caseRef(originalDto.getCaseRef())
            .recordingSource(originalDto.getRecordingSource())
            .filename(newFilename)
            .serviceCode(originalDto.getServiceCode())
            .courtLocationCode(originalDto.getCourtLocationCode())
            .segment(newSegment)
            .folder(originalDto.getFolder())
            .hearingRoomRef(originalDto.getHearingRoomRef())
            .jurisdictionCode(originalDto.getJurisdictionCode())
            .recordingRef(baseFileName)
            .sourceBlobUrl(originalDto.getSourceBlobUrl())
            .urlDomain(originalDto.getUrlDomain())
            .filenameExtension(originalDto.getFilenameExtension())
            .fileSize(originalDto.getFileSize())
            .checkSum(originalDto.getCheckSum())
            .interpreter(originalDto.getInterpreter())
            .recordingDateTime(originalDto.getRecordingDateTime())
            .build();
    }

    private File loadInputFile() {
        File inputFile;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test-upload-file.mp4")) {
            if (Objects.isNull(inputStream)) {
                throw new IllegalArgumentException("Invalid input file path");
            }
            inputFile = File.createTempFile("test-upload-file", ".mp4");
            Files.copy(inputStream, inputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load input file", e);
        }
        return inputFile;
    }

    private String addSegment(String baseFileName, int segment) {
        return String.format("%s_%d.mp4", baseFileName, segment);
    }

    private String getRandomElement(String[] array) {
        return array[RandomUtils.secure().randomInt(0, array.length)];
    }

    private void uploadFiles(File inputFile, List<HearingRecordingDto> recordingDtos) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = recordingDtos.stream()
                .map(recordingDto -> CompletableFuture.runAsync(() -> {
                    String fileName = recordingDto.getFilename();
                    BlobClient blobClient = hrsCvpBlobContainerClient.getBlobClient(fileName);
                    try (InputStream inputStream = new FileInputStream(inputFile)) {
                        blobClient.upload(inputStream, inputFile.length(), true);
                        String url = blobClient.getBlobUrl();
                        recordingDto.setSourceBlobUrl(url);
                    } catch (IOException e) {
                        LOGGER.error("Failed to upload file: {}", fileName, e);
                    }
                    try {
                        ccdUploadService.upload(recordingDto);
                    } catch (Exception e) {
                        LOGGER.error("Failed to upload recording to CCD: {}", recordingDto.getFilename(), e);
                    }
                }, executor))
                .toList();
            // Wait for all files to be uploaded
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            LOGGER.error("Error occurred during file upload", e);
        }
    }
}
