package uk.gov.hmcts.reform.em.hrs.config.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This Azure container is used to access CSV files generated by the Case Document Access Management team in order
 * to update the meta data of documents in bulk.
 */
@Configuration
public class MetaDataAzureStorageConfiguration {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.metadata-blob-container-reference}")
    private String containerReference;

    @Bean(name = "metadata-storage")
    @ConditionalOnProperty(
        value = "toggle.metadatamigration",
        havingValue = "true")
    BlobContainerClient cloudBlobContainer() throws UnknownHostException {
        final String blobAddress = connectionString.contains("azure-storage-emulator-azurite")
            ? connectionString.replace(
                "azure-storage-emulator-azurite",
                InetAddress.getByName("azure-storage-emulator-azurite").getHostAddress())
            : connectionString;

        final BlobContainerClient client = getClient(blobAddress);

        try {
            client.create();
            return client;
        } catch (BlobStorageException e) {
            return client;
        }
    }

    BlobContainerClient getClient(String blobAddress) {
        return new BlobContainerClientBuilder()
            .connectionString(blobAddress)
            .containerName(containerReference)
            .buildClient();
    }

}
