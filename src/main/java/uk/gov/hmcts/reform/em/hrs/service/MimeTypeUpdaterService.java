package uk.gov.hmcts.reform.em.hrs.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobRange;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.hrs.dto.SegmentMimeTypeTaskDTO;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class MimeTypeUpdaterService {

    private static final Logger LOGGER = getLogger(MimeTypeUpdaterService.class);
    private static final long MAX_DOWNLOAD_SIZE_BYTES = 2L * 1024 * 1024; // 2MB

    private final HearingRecordingSegmentRepository segmentRepository;
    private final BlobContainerClient blobContainerClient;
    private final Tika tika;

    public MimeTypeUpdaterService(
        HearingRecordingSegmentRepository segmentRepository,
        @Qualifier("cvpBlobContainerClient") BlobContainerClient blobContainerClient
    ) {
        this.segmentRepository = segmentRepository;
        this.blobContainerClient = blobContainerClient;
        this.tika = new Tika();
    }

    /**
     * Processes a list of segments within a single, new transaction.
     * If any segment fails, the entire transaction for the batch is rolled back.
     *
     * @param batch A list of SegmentData objects to process.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateMimeTypesForBatch(final List<SegmentMimeTypeTaskDTO> batch) {
        LOGGER.info("Starting new transaction for batch of {} segments.", batch.size());
        for (final SegmentMimeTypeTaskDTO segmentData : batch) {
            processSingleSegment(segmentData);
        }
        LOGGER.info("Successfully committed transaction for batch of {} segments.", batch.size());
    }

    /**
     * Helper method to process a single segment. This is not transactional on its own
     * but participates in the transaction started by the public calling method.
     */
    private void processSingleSegment(final SegmentMimeTypeTaskDTO segmentData) {
        final String filename = segmentData.filename();
        final UUID segmentId = segmentData.id();

        LOGGER.info("Processing segment ID: {}", segmentId);
        final BlobClient blobClient = blobContainerClient.getBlobClient(filename);

        if (Boolean.FALSE.equals(blobClient.exists())) {
            throw new IllegalStateException("Blob not found for filename: " + filename + " (Segment ID: " + segmentId + ")");
        }

        final String mimeType;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            blobClient.downloadStreamWithResponse(
                outputStream, new BlobRange(0, MAX_DOWNLOAD_SIZE_BYTES),
                null, null, false, null, null
            );
            mimeType = tika.detect(outputStream.toByteArray());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to download or detect MIME type for segment " + segmentId, e);
        }

        segmentRepository.updateMimeType(segmentId, mimeType);

        LOGGER.info(
            "Successfully updated segment {} with MIME type: {}",
            segmentId,
            mimeType
        );
    }
}
