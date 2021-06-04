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
import org.springframework.stereotype.Component;

import java.io.OutputStream;

@Component
public class BlobstoreClientImpl implements BlobstoreClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlobstoreClientImpl.class);

    private final BlobContainerClient blobContainerClient;

    @Autowired
    public BlobstoreClientImpl(@Qualifier("HrsBlobContainerClient") final BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
    }


    @Override
    public long getFileSize(String filename) {
        final BlockBlobClient blobClient = blobContainerClient.getBlobClient(filename).getBlockBlobClient();
        final long fileSize = blobClient.getProperties().getBlobSize();
        return fileSize;
    }

    @Override
    public BlobProperties getBlobProperties(final String filename) {
        return blobContainerClient.getBlobClient(filename).getProperties();
    }


    @Override
    public void downloadFile(final String filename, BlobRange blobRange,
                             final OutputStream outputStream) {

        blockBlobClient(filename.toString())
            .downloadWithResponse(
                outputStream,
                blobRange,
                new DownloadRetryOptions().setMaxRetryRequests(5),
                null,
                false,
                null,
                null
            );
    }


    private BlockBlobClient blockBlobClient(String id) {
        return blobContainerClient.getBlobClient(id).getBlockBlobClient();
    }

}
