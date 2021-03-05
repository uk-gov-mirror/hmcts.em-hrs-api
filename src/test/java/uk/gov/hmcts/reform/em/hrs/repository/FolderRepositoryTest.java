package uk.gov.hmcts.reform.em.hrs.repository;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import uk.gov.hmcts.reform.em.hrs.domain.Folder;

import java.util.Optional;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Sql({"/create-folder.sql"})
class FolderRepositoryTest extends AbstractRepositoryIntegrationTest {
    @Inject
    private FolderRepository underTest;

    @Test
    void testShouldReturnEmptySetWhenDatabaseIsEmpty() {
        final Optional<Folder> folder = underTest.findByName("folder-0");

        assertThat(folder).isEmpty();
    }

    @Test
    void testFindByFolderName() {
        final Optional<Folder> folder = underTest.findByName("folder-1");

        assertThat(folder).isPresent();
    }

}
