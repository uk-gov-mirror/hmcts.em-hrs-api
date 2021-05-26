package uk.gov.hmcts.reform.em.hrs.storage;

import com.azure.storage.blob.models.BlobProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface BlobstoreClient {

    void downloadFile(String filename, HttpServletRequest request, HttpServletResponse output);

    BlobProperties getBlobProperties(final String filename);
}
