package uk.gov.hmcts.reform.em.hrs.testutil;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.BaseTest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import static uk.gov.hmcts.reform.em.hrs.testutil.SleepHelper.sleepForSeconds;

@Component
public class BlobUtil {

    public static final int FIND_BLOB_TIMEOUT = 3;
    public final BlobContainerClient hrsBlobContainerClient;
    public final BlobContainerClient cvpBlobContainerClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    @Autowired
    public BlobUtil(@Qualifier("hrsBlobContainerClient") BlobContainerClient hrsBlobContainerClient,
                    @Qualifier("cvpBlobContainerClient") BlobContainerClient cvpBlobContainerClient) {
        this.hrsBlobContainerClient = hrsBlobContainerClient;
        this.cvpBlobContainerClient = cvpBlobContainerClient;
    }

    public void deleteFilesFromContainerNotMatchingPrefix(final String folderName, BlobContainerClient containerClient,
                                                          String fileNamePrefixToNotDelete) {

        String pathPrefix = folderName + "/" + fileNamePrefixToNotDelete;
        LOGGER.info("Cleaning folder: {}", folderName);
        LOGGER.info("Excluding Prefix: {}", pathPrefix);


        containerClient.listBlobs()
            .stream()
            .filter(blobItem ->
                        blobItem.getName().startsWith(folderName) &&
                            !blobItem.getName().startsWith(pathPrefix)
            )
            .forEach(blobItem -> {
                LOGGER.info("Deleting old blob: {}", blobItem.getName());
                final BlockBlobClient blobClient =
                    containerClient.getBlobClient(blobItem.getName()).getBlockBlobClient();
                blobClient.delete();
            });
    }

    public void checkIfBlobUploadedToCvp(final String folderName, int originalBlobCount) {
        checkIfUploadedToStore(folderName, originalBlobCount, 1, cvpBlobContainerClient);
    }

    public void checkIfUploadedToHrsStorage(final String folderName, int blobCount) {
        checkIfUploadedToStore(folderName, blobCount, 1, hrsBlobContainerClient);
    }


    public void checkIfUploadedToStore(final String folderName, int originalBlobCount, int expectedNewBlobs,
                                       BlobContainerClient containerClient) {
        int expectedTotalBlobs = originalBlobCount + expectedNewBlobs;
        int retryCount = 0;
        int currentBlobCount = originalBlobCount;
        while (retryCount <= 20 && currentBlobCount < expectedTotalBlobs) {
            sleepForSeconds(FIND_BLOB_TIMEOUT);
            LOGGER.debug(
                "hrsBlobContainerClient.getBlobContainerUrl() ~{}",
                containerClient.getBlobContainerUrl()
            );
            currentBlobCount = getBlobCount(containerClient, folderName);
            retryCount++;
        }
        if (retryCount > 20) {
            throw new IllegalStateException(
                "Could not find files within test.\nOriginal count =" + originalBlobCount + ", Expected Total = " +
                    expectedTotalBlobs + ", Last Count=" + currentBlobCount);
        }
    }

    public int getBlobCount(BlobContainerClient client, String folderName) {
        return (int) client.listBlobs()
            .stream()
            .filter(blobItem -> blobItem.getName().startsWith(folderName)).count();
    }


    public void uploadToCvpContainer(final String blobName) throws Exception {
        final FileInputStream fileInputStream = getTestFile();
        final byte[] bytes = fileInputStream.readAllBytes();
        final InputStream inStream = new ByteArrayInputStream(bytes);

        final BlobClient blobClient = cvpBlobContainerClient.getBlobClient(blobName);
        LOGGER.debug("cvpBlobContainerClient.getBlobContainerUrl() ~{}", cvpBlobContainerClient.getBlobContainerUrl());
        blobClient.upload(new BufferedInputStream(inStream), bytes.length);
    }

    public FileInputStream getTestFile() throws Exception {
        final URL resource = BlobUtil.class.getClassLoader().getResource("data/test_data.mp4");
        final File file = new File(Objects.requireNonNull(resource).toURI());
        return new FileInputStream(file);
    }
}
