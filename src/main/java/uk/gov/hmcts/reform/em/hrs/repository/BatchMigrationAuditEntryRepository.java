package uk.gov.hmcts.reform.em.hrs.repository;

import org.springframework.data.repository.CrudRepository;
import uk.gov.hmcts.reform.em.hrs.domain.BatchMigrationAuditEntry;

public interface BatchMigrationAuditEntryRepository extends CrudRepository<BatchMigrationAuditEntry, Long> {
}
