package uk.gov.hmcts.reform.em.hrs.storage;

import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlobRange;

import java.io.OutputStream;

public interface BlobstoreClient {

    long getFileSize(String filename);

    void downloadFile(String filename, BlobRange blobRange, final OutputStream outputStream);

    BlobProperties getBlobProperties(final String filename);
}
