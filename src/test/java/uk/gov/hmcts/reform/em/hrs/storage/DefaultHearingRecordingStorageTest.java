package uk.gov.hmcts.reform.em.hrs.storage;

import org.testcontainers.containers.GenericContainer;

class DefaultHearingRecordingStorageTest {
    private final String azuriteImage = "mcr.microsoft.com/azure-storage/azurite";

    //docker run -p 10000:10000 mcr.microsoft.com/azure-storage/azurite azurite-blob --blobHost 0.0.0.0 --blobPort 10000
    private final GenericContainer<?> azuriteContainer = new GenericContainer<>(azuriteImage)
        .withExposedPorts(10000)
        .withCommand("azurite-blob --blobHost 0.0.0.0 --blobPort 10000");

}
