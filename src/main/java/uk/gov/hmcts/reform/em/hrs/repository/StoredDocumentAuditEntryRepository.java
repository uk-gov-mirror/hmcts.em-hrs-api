package uk.gov.hmcts.reform.em.hrs.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.hrs.domain.StoredDocument;
import uk.gov.hmcts.reform.em.hrs.domain.StoredDocumentAuditEntry;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoredDocumentAuditEntryRepository extends PagingAndSortingRepository<StoredDocumentAuditEntry, UUID> {

    List<StoredDocumentAuditEntry> findByStoredDocumentOrderByRecordedDateTimeAsc(StoredDocument storedDocument);

}
