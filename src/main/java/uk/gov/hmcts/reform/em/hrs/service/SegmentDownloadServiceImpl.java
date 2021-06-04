package uk.gov.hmcts.reform.em.hrs.service;

import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlobRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.domain.AuditActions;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.exception.InvalidRangeRequestException;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;
import uk.gov.hmcts.reform.em.hrs.storage.BlobstoreClient;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

        //        logHttpHeaders(request);//keep during early life support

        String rangeHeader = getHTTPHeaderCaseSafe(request, HttpHeaders.RANGE);
        LOGGER.info("Range header for filename {} = ", filename, rangeHeader);

        BlobRange blobRange = null;
        if (rangeHeader != null) {
            long fileSize = blobstoreClient.getFileSize(filename);
            response.setStatus(HttpStatus.PARTIAL_CONTENT.value());


            String patternString = "^bytes=\\d*-\\d*";

            Pattern pattern = Pattern.compile(patternString);

            Matcher matcher = pattern.matcher(rangeHeader);

            if (!matcher.matches()) {

                throw new InvalidRangeRequestException(response, fileSize);
            }


            // Range headers can request a multipart range but this is not to be supported yet
            // https://developer.mozilla.org/en-US/docs/Web/HTTP/Range_requests
            // Take only first requested range and process it
            String byteRange = rangeHeader.substring(6).split(",")[0].trim();

            blobRange = setContentRangeHeadersAndGenerateBlobRange(byteRange, fileSize, response);


            Long blobRangeCount = blobRange.getCount();
            long blobRangeOffset = blobRange.getOffset();
            long blobRangeEndInclusive = blobRangeCount - 1;

            String contentRangeResponse = "bytes " + blobRangeOffset + "-" + blobRangeEndInclusive + "/" + fileSize;

            response.setHeader(HttpHeaders.CONTENT_RANGE, contentRangeResponse);
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(blobRangeCount));

            LOGGER.debug("Calc Blob Values: blobStart {}, blobLength {}", blobRangeOffset, blobRangeCount);
            LOGGER.debug(
                "Calc Http Header Values: CONTENT_RANGE {}, CONTENT_LENGTH {}",
                request.getHeader(HttpHeaders.CONTENT_RANGE),
                request.getHeader(HttpHeaders.CONTENT_LENGTH)
            );

        }


        ServletOutputStream outputStream = response.getOutputStream();

        blobstoreClient.downloadFile(filename, blobRange, outputStream);
        HearingRecordingSegment segment = segmentRepository.findByFilename(filename);
        auditEntryService.createAndSaveEntry(segment, AuditActions.USER_DOWNLOAD_OK);

    }

    private void logHttpHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();

            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                LOGGER.info("HeaderName , Values: {} , {}", headerName, headerValue);
            }
        }

    }


    //non frontdoor environments (ie DEMO do not use front door, and may use TitleCase header names)
    //front door environments use lowercase header names
    private String getHTTPHeaderCaseSafe(HttpServletRequest request, String header) {
        String anyCase = request.getHeader(header);
        if (anyCase == null) {
            anyCase = request.getHeader(header.toLowerCase());
        }
        return anyCase;
    }


    private BlobRange setContentRangeHeadersAndGenerateBlobRange(String part, Long fileSize,
                                                                 HttpServletResponse response) {
        //part example = "25165824-33554431"
        long byteRangeStart = extractLongFromSubstring(part, 0, part.indexOf('-'));
        long byteRangeEnd = extractLongFromSubstring(part, part.indexOf('-') + 1, part.length());

        if (byteRangeStart == -1) {
            byteRangeStart = fileSize - byteRangeEnd;
            byteRangeEnd = fileSize;
        } else if (byteRangeEnd == -1 || byteRangeEnd > fileSize) {
            byteRangeEnd = fileSize;
        }

        // Check if Range is syntactically valid. If not, then return 416.
        if (byteRangeStart > byteRangeEnd) {
            LOGGER.info("Invalid Range Request, start is greater than en ");
            throw new InvalidRangeRequestException(response, fileSize);
        }

        long byteRangeCount = (byteRangeEnd - byteRangeStart) + 1;
        BlobRange blobRange = new BlobRange(byteRangeStart, byteRangeCount);


        if (blobRange.getOffset() == 0 && blobRange.getCount() > fileSize) {
            LOGGER.info("WILL HAVE TO LOAD FULL BLOB, as Offset =0 and blobrange count > fileSize");
            return null;
        }


        return blobRange;
    }

    private long extractLongFromSubstring(String value, int beginIndex, int endIndex) {
        String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
    }
}
