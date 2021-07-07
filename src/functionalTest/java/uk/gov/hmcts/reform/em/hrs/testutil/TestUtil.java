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
import java.util.concurrent.TimeUnit;

@Component
public class TestUtil {

    private final BlobContainerClient hrsBlobContainerClient;
    private final BlobContainerClient cvpBlobContainerClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    @Autowired
    public TestUtil(@Qualifier("hrsBlobContainerClient") BlobContainerClient hrsBlobContainerClient,
                    @Qualifier("cvpBlobContainerClient") BlobContainerClient cvpBlobContainerClient) {
        this.hrsBlobContainerClient = hrsBlobContainerClient;
        this.cvpBlobContainerClient = cvpBlobContainerClient;
    }

    public void deleteFileFromHrsContainer(final String folderName) {
        hrsBlobContainerClient.listBlobs()
            .stream()
            .filter(blobItem -> blobItem.getName().startsWith(folderName))
            .forEach(blobItem -> {
                final BlockBlobClient blobClient = hrsBlobContainerClient.getBlobClient(blobItem.getName()).getBlockBlobClient();
                blobClient.delete();
            });
    }

    public void checkIfUploadedToCvp(final String folderName, int blobCount) throws InterruptedException {
        int expectedBlobs = blobCount + 1;
        int count = 0;
        while (count <= 10 && blobCount < expectedBlobs) {
            TimeUnit.SECONDS.sleep(30);
            LOGGER.info("cvpBlobContainerClient.getBlobContainerUrl() ~{}",
                        cvpBlobContainerClient.getBlobContainerUrl());
            blobCount = getCvpBlobCount(folderName);
            count++;
        }
        if (count > 10) {
            throw new IllegalStateException("could not find files within test");
        }
    }

    public void checkIfUploadedToHrs(final String folderName, int blobCount) throws InterruptedException {
        int expectedBlobs = blobCount + 1;
        int count = 0;
        while (count <= 20 && blobCount < expectedBlobs) {
            TimeUnit.SECONDS.sleep(30);
            LOGGER.info("hrsBlobContainerClient.getBlobContainerUrl() ~{}",
                        hrsBlobContainerClient.getBlobContainerUrl());
            blobCount = getHrsBlobCount(folderName);
            count++;
        }
        if (count > 20) {
            throw new IllegalStateException("could not find files within test");
        }
    }

    public int getCvpBlobCount(String folderName) {
        return getBlobCount(cvpBlobContainerClient, folderName);
    }

    public int getHrsBlobCount(String folderName) {
        return getBlobCount(hrsBlobContainerClient, folderName);
    }

    private int getBlobCount(BlobContainerClient client, String folderName) {
        return  (int) client.listBlobs()
            .stream()
            .filter(blobItem -> blobItem.getName().startsWith(folderName)).count();
    }

    public void deleteFileFromCvpContainer(final String folderName) {
        cvpBlobContainerClient.listBlobs()
            .stream()
            .filter(blobItem -> blobItem.getName().startsWith(folderName))
            .forEach(blobItem -> {
                final BlockBlobClient blobClient = cvpBlobContainerClient.getBlobClient(blobItem.getName()).getBlockBlobClient();
                blobClient.delete();
            });
    }

    public void uploadToCvpContainer(final String blobName) throws Exception {
        final FileInputStream fileInputStream = getTestFile();
        final byte[] bytes = fileInputStream.readAllBytes();
        final InputStream inStream = new ByteArrayInputStream(bytes);

        final BlobClient blobClient = cvpBlobContainerClient.getBlobClient(blobName);
        LOGGER.info("cvpBlobContainerClient.getBlobContainerUrl() ~{}" , cvpBlobContainerClient.getBlobContainerUrl());
        blobClient.upload(new BufferedInputStream(inStream), bytes.length);
    }

    public FileInputStream getTestFile() throws Exception {
        final URL resource = TestUtil.class.getClassLoader().getResource("data/test_data.mp4");
        final File file = new File(Objects.requireNonNull(resource).toURI());
        return new FileInputStream(file);
    }
}
