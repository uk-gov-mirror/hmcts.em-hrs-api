package uk.gov.hmcts.reform.em.hrs.storage;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlobRange;
import com.azure.storage.blob.models.DownloadRetryOptions;
import com.azure.storage.blob.specialized.BlockBlobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.exception.InvalidRangeRequestException;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class BlobstoreClientImpl implements BlobstoreClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlobstoreClientImpl.class);
    private static final int DEFAULT_BUFFER_SIZE = 20480; // ..bytes = 20KB.
    private final BlobContainerClient blobContainerClient;

    @Autowired
    public BlobstoreClientImpl(@Qualifier("HrsBlobContainerClient") final BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
    }

    @Override
    public void downloadFile(final String filename, HttpServletRequest request,
                             final HttpServletResponse response) {
        final BlockBlobClient blobClient = blobContainerClient.getBlobClient(filename).getBlockBlobClient();


        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();

            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                LOGGER.info("HeaderName , Values: {} , {}", headerName, headerValue);
            }

        }


        String rangeHeader = request.getHeader("range");
        if (rangeHeader == null) {
            LOGGER.info("Range Header is null");

            try {
                loadFullBlob(filename, blobClient, response);
            } catch (Exception e) {
                LOGGER.warn("Full blob download Exception is: {}", e);
            }
        } else {
            LOGGER.info("Range Header is not null, value: {}", rangeHeader);

            try {
                LOGGER.info("Loading Partial Range");
                final long fileSize = blobClient.getProperties().getBlobSize();
                loadPartialBlob(filename, fileSize, blobClient, request, response);
                LOGGER.info("Completed Partial Range");
            } catch (IOException e) {
                LOGGER.error("Error in downloading partial range {}", e);
            }
        }


    }

    @Override
    public BlobProperties getBlobProperties(final String filename) {
        return blobContainerClient.getBlobClient(filename).getProperties();
    }


    private void loadFullBlob(String filename, BlockBlobClient blobClient, HttpServletResponse response)
        throws IOException {

        LOGGER.info("Loading Full Blob {}", filename);

        response.setHeader(HttpHeaders.ACCEPT_RANGES.toLowerCase(Locale.ROOT), "bytes");
        blobClient.download(response.getOutputStream());
        LOGGER.info("Loading Full Blob Method end {}", filename);
    }


    private void loadPartialBlob(String filename, Long fileSize, BlockBlobClient blobClient,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {

        LOGGER.info("Range header provided {}", filename);

        String rangeHeader = request.getHeader("range");
        LOGGER.info("Range requested: {}", rangeHeader);


        response.setStatus(HttpStatus.PARTIAL_CONTENT.value());


        String patternString = "^bytes=\\d*-\\d*";

        Pattern pattern = Pattern.compile(patternString);

        Matcher matcher = pattern.matcher(rangeHeader);

        if (!matcher.matches()) {
            throw new InvalidRangeRequestException(response, fileSize);
        }

        response.setBufferSize(DEFAULT_BUFFER_SIZE);

        // Range headers can request a multipart range but this is not to be supported yet
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Range_requests
        // Take only first requested range and process it
        String byteRange = rangeHeader.substring(6).split(",")[0].trim();

        BlobRange blobRange = setContentRangeHeadersAndGenerateBlobRange(byteRange, fileSize, response);


        if (blobRange.getOffset() == 0 && blobRange.getCount() > fileSize) {
            LOGGER.info("WILL HAVE TO LOAD FULL BLOB, as Offset =0 and b count > fileSize {}", filename);
            loadFullBlob(filename, blobClient, response);
            return;
        }

        response.setStatus(HttpStatus.PARTIAL_CONTENT.value());


        LOGGER.info("Processing blob range: {}", blobRange.toString());
        blockBlobClient(filename.toString())
            .downloadWithResponse(
                response.getOutputStream(),
                blobRange,
                new DownloadRetryOptions().setMaxRetryRequests(5),
                null,
                false,
                null,
                null
            );
    }

    private BlobRange setContentRangeHeadersAndGenerateBlobRange(String part, Long fileSize,
                                                                 HttpServletResponse response) {
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

        long byteRangeLength = (byteRangeEnd - byteRangeStart) + 1;
        String contentRangeResponse = "bytes " + byteRangeStart + "-" + byteRangeEnd + "/" + fileSize;
        String contentLengthResponse = String.valueOf(byteRangeLength);

        LOGGER.info("Calc Blob Values: blobStart {}, blobLength {}", byteRangeStart, byteRangeLength);
        LOGGER.info("Calc Header Values: range {}, fileSize {}", contentRangeResponse, contentLengthResponse);


        response.setHeader(HttpHeaders.ACCEPT_RANGES.toLowerCase(Locale.ROOT), "bytes");
        response.setHeader(HttpHeaders.CONTENT_RANGE.toLowerCase(Locale.ROOT), contentRangeResponse);
        response.setHeader(HttpHeaders.CONTENT_LENGTH.toLowerCase(Locale.ROOT), contentLengthResponse);
        return new BlobRange(byteRangeStart, byteRangeLength);
    }

    private long extractLongFromSubstring(String value, int beginIndex, int endIndex) {
        String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
    }

    private BlockBlobClient blockBlobClient(String id) {
        return blobContainerClient.getBlobClient(id).getBlockBlobClient();
    }

}
