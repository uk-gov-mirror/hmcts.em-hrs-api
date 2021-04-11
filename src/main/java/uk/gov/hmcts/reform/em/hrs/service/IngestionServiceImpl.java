package uk.gov.hmcts.reform.em.hrs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;
import uk.gov.hmcts.reform.em.hrs.service.ccd.CcdDataStoreApiClient;
import uk.gov.hmcts.reform.em.hrs.storage.HearingRecordingStorage;
import uk.gov.hmcts.reform.em.hrs.util.Snooper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@Transactional
public class IngestionServiceImpl implements IngestionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IngestionServiceImpl.class);

    private final CcdDataStoreApiClient ccdDataStoreApiClient;
    private final HearingRecordingRepository recordingRepository;
    private final HearingRecordingSegmentRepository segmentRepository;
    private final HearingRecordingStorage hearingRecordingStorage;
    private final Snooper snooper;

    @Inject
    public IngestionServiceImpl(final CcdDataStoreApiClient ccdDataStoreApiClient,
                                final HearingRecordingRepository recordingRepository,
                                final HearingRecordingSegmentRepository segmentRepository,
                                final HearingRecordingStorage hearingRecordingStorage,
                                final Snooper snooper) {
        this.ccdDataStoreApiClient = ccdDataStoreApiClient;
        this.recordingRepository = recordingRepository;
        this.segmentRepository = segmentRepository;
        this.hearingRecordingStorage = hearingRecordingStorage;
        this.snooper = snooper;
    }

    @Override
    @Async("HrsAsyncExecutor")
    public void ingest(final HearingRecordingDto hearingRecordingDto) {

        final CompletableFuture<Void> metadataFuture = CompletableFuture.runAsync(() -> {
            final Optional<HearingRecording> optionalHearingRecording = recordingRepository.findByRecordingRef(
                hearingRecordingDto.getRecordingRef()
            );

            final HearingRecordingSegment segment = optionalHearingRecording
                .map(x -> updateCase(x, hearingRecordingDto))
                .orElseGet(() -> createCaseAndPersist(hearingRecordingDto));

            segmentRepository.save(segment);
        });

        final CompletableFuture<Void> blobCopyFuture = CompletableFuture.runAsync(
            () -> hearingRecordingStorage.copyRecording(
                hearingRecordingDto.getCvpFileUrl(),
                hearingRecordingDto.getFilename()
            )
        );

        try {
            CompletableFuture
                .allOf(metadataFuture, blobCopyFuture)
                .get();
        } catch (ExecutionException e) {
            snoop(hearingRecordingDto.getCvpFileUrl(), e);
        } catch (InterruptedException e) {
            snoop(hearingRecordingDto.getCvpFileUrl(), e);
            Thread.currentThread().interrupt();
        }
    }

    private HearingRecordingSegment updateCase(final HearingRecording recording,
                                               final HearingRecordingDto recordingDto) {

        LOGGER.info("adding  recording ({}) to case({})", recordingDto.getRecordingRef(), recording.getCcdCaseId());

        ccdDataStoreApiClient.updateCaseData(recording.getCcdCaseId(), recordingDto);

        return HearingRecordingSegment.builder()
            .filename(recordingDto.getFilename())
            .fileExtension(recordingDto.getFilenameExtension())
            .fileSizeMb(recordingDto.getFileSize())
            .fileMd5Checksum(recordingDto.getCheckSum())
            .ingestionFileSourceUri(recordingDto.getCvpFileUrl())
            .recordingSegment(recordingDto.getSegment())
            .hearingRecording(recording)
            .build();
    }

    private HearingRecordingSegment createCaseAndPersist(final HearingRecordingDto recordingDto) {
        LOGGER.info("creating a new case for recording: {}", recordingDto.getRecordingRef());

        final Long caseId = ccdDataStoreApiClient.createCase(recordingDto);

        final HearingRecording recording = HearingRecording.builder()
            .recordingRef(recordingDto.getRecordingRef())
            .ccdCaseId(caseId)
            .caseRef(recordingDto.getCaseRef())
            .hearingLocationCode(recordingDto.getCourtLocationCode())
            .hearingRoomRef(recordingDto.getHearingRoomRef())
            .hearingSource(recordingDto.getRecordingSource())
            .jurisdictionCode(recordingDto.getJurisdictionCode())
            .serviceCode(recordingDto.getServiceCode())
            .build();

        final HearingRecording savedRecording = recordingRepository.save(recording);

        return HearingRecordingSegment.builder()
            .filename(recordingDto.getFilename())
            .fileExtension(recordingDto.getFilenameExtension())
            .fileSizeMb(recordingDto.getFileSize())
            .fileMd5Checksum(recordingDto.getCheckSum())
            .ingestionFileSourceUri(recordingDto.getCvpFileUrl())
            .recordingSegment(recordingDto.getSegment())
            .hearingRecording(savedRecording)
            .build();
    }

    private void snoop(final String file, final Throwable throwable) {
        final String message = String.format("An error occurred ingesting file '%s'", file);
        snooper.snoop(message, throwable);
    }

}
