package uk.gov.hmcts.reform.em.hrs.storage;

import com.azure.core.http.rest.PagedIterable;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.models.UserDelegationKey;
import com.azure.storage.blob.options.BlobBeginCopyOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import uk.gov.hmcts.reform.em.hrs.util.Snooper;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class DefaultHearingRecordingStorage implements HearingRecordingStorage {
    private static final int BLOB_LIST_TIMEOUT = 5;
    private final BlobContainerAsyncClient hrsBlobContainerAsyncClient;
    private final BlobContainerClient hrsBlobContainerClient;
    private final Snooper snooper;

    @Inject
    public DefaultHearingRecordingStorage(final BlobContainerAsyncClient hrsContainerAsyncClient,
                                          final @Named("HrsBlobContainerClient") BlobContainerClient hrsContainerClient,
                                          final Snooper snooper) {
        this.hrsBlobContainerAsyncClient = hrsContainerAsyncClient;
        this.hrsBlobContainerClient = hrsContainerClient;
        this.snooper = snooper;
    }

    @Override
    public Set<String> findByFolder(final String folderName) {
        final BlobListDetails blobListDetails = new BlobListDetails()
            .setRetrieveDeletedBlobs(false)
            .setRetrieveSnapshots(false);
        final ListBlobsOptions options = new ListBlobsOptions()
            .setDetails(blobListDetails)
            .setPrefix(folderName);
        final Duration duration = Duration.ofMinutes(BLOB_LIST_TIMEOUT);

        final PagedIterable<BlobItem> blobItems = hrsBlobContainerClient.listBlobs(options, duration);

        return blobItems.streamByPage()
            .flatMap(x -> x.getValue().stream().map(BlobItem::getName))
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void copyRecording(final String sourceUri, final String filename) {
        final BlobBeginCopyOptions blobBeginCopyOptions = new BlobBeginCopyOptions(sourceUri);

        //        BlobBeginCopySourceRequestConditions s=new BlobBeginCopySourceRequestConditions();
        //        blobBeginCopyOptions.setSourceRequestConditions(s);

        final BlobAsyncClient destBlobAsyncClient = hrsBlobContainerAsyncClient.getBlobAsyncClient(filename);
        destBlobAsyncClient.beginCopy(blobBeginCopyOptions)
            .subscribe(
                x -> {
                },
                y -> snooper.snoop(String.format("File %s copied failed:: %s", filename, y.getMessage())),
                () -> snooper.snoop(String.format("File %s copied successfully", filename))
            );
    }


    public void temp() {

        //somehow we need to get a SAS token from the CVP file source URL, and then append it the sourceURism as per
        // below example

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .endpoint("https://<>.blob.core.windows.net/")
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildClient();
        // get User Delegation Key
        OffsetDateTime delegationKeyStartTime = OffsetDateTime.now();
        OffsetDateTime delegationKeyExpiryTime = OffsetDateTime.now().plusDays(7);
        UserDelegationKey
            key = blobServiceClient.getUserDelegationKey(delegationKeyStartTime, delegationKeyExpiryTime);

        BlobContainerClient sourceContainerClient = blobServiceClient.getBlobContainerClient("test");


        BlobClient sourceBlob = sourceContainerClient.getBlobClient("test.mp3");
        // generate sas token
        OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(1);
        BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);

        BlobServiceSasSignatureValues myValues = new BlobServiceSasSignatureValues(expiryTime, permission)
            .setStartTime(OffsetDateTime.now());
        String sas = sourceBlob.generateUserDelegationSas(myValues, key);

        // copy
        BlobServiceClient desServiceClient = new BlobServiceClientBuilder()
            .endpoint("https://<>.blob.core.windows.net/")
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildClient();
        BlobContainerClient desContainerClient = blobServiceClient.getBlobContainerClient("test");
        String res = desContainerClient.getBlobClient("test.mp3")
            .copyFromUrl(sourceBlob.getBlobUrl() + "?" + sas);
        System.out.println(res);

    }


}
