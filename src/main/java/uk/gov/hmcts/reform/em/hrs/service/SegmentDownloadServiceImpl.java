package uk.gov.hmcts.reform.em.hrs.service;

import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlobRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.domain.AuditActions;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.exception.InvalidRangeRequestException;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;
import uk.gov.hmcts.reform.em.hrs.storage.BlobstoreClient;
import uk.gov.hmcts.reform.em.hrs.util.HttpHeaderProcessor;
import uk.gov.hmcts.reform.em.hrs.util.debug.HttpHeadersLogging;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class SegmentDownloadServiceImpl implements SegmentDownloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SegmentDownloadServiceImpl.class);
    private static final int DEFAULT_BUFFER_SIZE = 20480; // ..bytes = 20KB.
    private final HearingRecordingSegmentRepository segmentRepository;
    private final BlobstoreClient blobstoreClient;
    private final AuditEntryService auditEntryService;

    @Autowired
    public SegmentDownloadServiceImpl(HearingRecordingSegmentRepository segmentRepository,
                                      BlobstoreClient blobstoreClient, AuditEntryService auditEntryService) {
        this.segmentRepository = segmentRepository;
        this.blobstoreClient = blobstoreClient;
        this.auditEntryService = auditEntryService;
    }

    @Override
    @PreAuthorize("hasPermission(#recordingId,'READ')")
    public Map<String, String> getDownloadInfo(UUID recordingId, Integer segmentNo) {

        HearingRecordingSegment segment =
            segmentRepository.findByHearingRecordingIdAndRecordingSegment(recordingId, segmentNo);

        auditEntryService.createAndSaveEntry(segment, AuditActions.USER_DOWNLOAD_REQUESTED);

        BlobProperties blobProperties = blobstoreClient.getBlobProperties(segment.getFilename());

        LOGGER.info(
            "downloading blob with the following properties: [filenmae: {}, content-type: {}, content-length: {}]",
            segment.getFilename(), blobProperties.getContentType(), blobProperties.getBlobSize()
        );

        return Map.of(
            "filename", segment.getFilename(),
            "contentType", blobProperties.getContentType(),
            "contentLength", String.valueOf(blobProperties.getBlobSize())
        );
    }

    @Override
    @PreAuthorize("hasPermission(#recordingId,'READ')")
    public void download(String filename, HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", filename)
        );
        response.setBufferSize(DEFAULT_BUFFER_SIZE);

        HttpHeadersLogging.logHttpHeaders(request);//keep during early life support

        String rangeHeader = HttpHeaderProcessor.getHttpHeaderByCaseSensitiveAndLowerCase(request, HttpHeaders.RANGE);
        LOGGER.info("Range header for filename {} = ", filename, rangeHeader);

        BlobRange blobRange = null;
        if (rangeHeader != null) {
            long fileSize = blobstoreClient.getFileSize(filename);
            try {
                response.setStatus(HttpStatus.PARTIAL_CONTENT.value());

                // Range headers can request a multipart range but this is not to be supported yet
                // https://developer.mozilla.org/en-US/docs/Web/HTTP/Range_requests
                // Will only accept 1 range first requested range and process it
                List<HttpRange> byteRanges = HttpRange.parseRanges(rangeHeader);
                HttpRange firstByteRange = byteRanges.get(0);

                long byteRangeStart = firstByteRange.getRangeStart(fileSize);
                long byteRangeEnd = firstByteRange.getRangeEnd(fileSize);
                long byteRangeCount = (byteRangeEnd - byteRangeStart) + 1;

                blobRange = new BlobRange(byteRangeStart, byteRangeCount);

                String contentRangeResponse = "bytes " + byteRangeStart + "-" + byteRangeEnd + "/" + fileSize;

                response.setHeader(HttpHeaders.CONTENT_RANGE, contentRangeResponse);
                response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(byteRangeCount));

                LOGGER.debug(
                    "Calc Blob Values: blobStart {}, blobLength {}",
                    blobRange.getOffset(),
                    blobRange.getCount()
                );
                LOGGER.debug(
                    "Calc Http Header Values: CONTENT_RANGE {}, CONTENT_LENGTH {}",
                    request.getHeader(HttpHeaders.CONTENT_RANGE),
                    request.getHeader(HttpHeaders.CONTENT_LENGTH)
                );
            } catch (Exception e) {
                throw new InvalidRangeRequestException(response, fileSize);
            }
        }


        ServletOutputStream outputStream = response.getOutputStream();

        blobstoreClient.downloadFile(filename, blobRange, outputStream);
        HearingRecordingSegment segment = segmentRepository.findByFilename(filename);
        auditEntryService.createAndSaveEntry(segment, AuditActions.USER_DOWNLOAD_OK);

    }

}
