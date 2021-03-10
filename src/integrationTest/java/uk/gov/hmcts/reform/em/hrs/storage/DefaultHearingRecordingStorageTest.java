package uk.gov.hmcts.reform.em.hrs.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.em.hrs.config.TestAzureStorageConfiguration;
import uk.gov.hmcts.reform.em.hrs.helper.AzureOperations;

import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = {TestAzureStorageConfiguration.class, DefaultHearingRecordingStorage.class, AzureOperations.class}
)
class DefaultHearingRecordingStorageTest {
    @Inject
    private AzureOperations azureOperations;
    @Inject
    private DefaultHearingRecordingStorage underTest;

    private static final String ZERO_ITEM_FOLDER = "folder-0";
    private static final String ONE_ITEM_FOLDER = "folder-1";
    private static final String MANY_ITEMS_FOLDER = "folder-2";

    @BeforeEach
    void setup() {
        azureOperations.clearContainer();
    }

    @Test
    void testShouldReturnEmptySetWhenFolderDoesNotExist() {
        final Set<String> files = underTest.findByFolder(ZERO_ITEM_FOLDER);

        assertThat(files).isEmpty();
    }

    @Test
    void testShouldReturnASetContainingOneWhenFolderContainsOneItem() {
        final String filePath = ONE_ITEM_FOLDER + "/" + UUID.randomUUID().toString() + ".txt";
        azureOperations.uploadToContainer(filePath);

        final Set<String> files = underTest.findByFolder(ONE_ITEM_FOLDER);

        assertThat(files).singleElement().isEqualTo(filePath);
    }

    @Test
    void testShouldReturnSetContainingMultipleFilenamesWhenFolderContainsMultipleItems() {
        final Set<String> filePaths = generateFilePaths();
        azureOperations.populateContainer(filePaths);

        final Set<String> files = underTest.findByFolder(MANY_ITEMS_FOLDER);

        assertThat(files).hasSameElementsAs(filePaths);
    }

    private Set<String> generateFilePaths() {
        final Random random = new Random();
        final int number = random.nextInt(8) + 2;

        return IntStream.rangeClosed(1, number)
            .mapToObj(x -> MANY_ITEMS_FOLDER + "/f" + x + ".txt")
            .collect(Collectors.toUnmodifiableSet());
    }
}
