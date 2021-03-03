package uk.gov.hmcts.reform.em.hrs.repository;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.hrs.domain.Folder;

import java.util.List;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

class FolderRepositoryTest extends AbstractRepositoryIntegrationTest {
    @Inject
    private FolderRepository underTest;

    private static final String ZERO_ITEM_FOLDER = "folder-0";

    @Test
    void testShouldReturnEmptySetWhenDatabaseIsEmpty() {
        final List<Folder> folders = underTest.findByName(ZERO_ITEM_FOLDER);

        assertThat(folders).isEmpty();
    }
}
