package uk.gov.hmcts.reform.em.hrs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.hrs.domain.MigrateEntry;

import java.util.UUID;

@Repository
public interface MigrateEntryRepository extends JpaRepository<MigrateEntry, UUID> {
}
