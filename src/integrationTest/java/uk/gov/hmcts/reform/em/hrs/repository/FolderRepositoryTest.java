package uk.gov.hmcts.reform.em.hrs.repository;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import uk.gov.hmcts.reform.em.hrs.domain.Folder;

import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Sql({"/data/create-folder.sql"})
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

        assertThat(folder).hasValueSatisfying(x -> {
            assertThat(x.getId()).isEqualTo(UUID.fromString("3E3F63FB-3C7A-447B-86DA-69ED164763B0"));
            assertThat(x.getName()).isEqualTo("folder-1");
            assertThat(x.getJobsInProgress().size()).isEqualTo(2);
            assertThat(x.getHearingRecordings().size()).isEqualTo(2);
        });
    }

}
