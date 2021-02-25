package uk.gov.hmcts.reform.em.hrs.storage;

import com.azure.storage.blob.BlobContainerClient;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Set;

@Named
public class DefaultHearingRecordingStorage implements HearingRecordingStorage {
//    private final BlobContainerClient blobContainerClient;
//
//    @Inject
//    public DefaultHearingRecordingStorage(BlobContainerClient blobContainerClient) {
//        this.blobContainerClient = blobContainerClient;
//    }

    @Override
    public Set<String> findByFolder(String folderName) {
        return null;
    }
}
