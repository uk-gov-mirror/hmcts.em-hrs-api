package uk.gov.hmcts.reform.em.hrs.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.hrs.domain.Folder;

import java.util.List;
import java.util.UUID;

@Repository
public interface FolderRepository extends PagingAndSortingRepository<Folder, UUID> {

    @Query("FROM Folder AS f JOIN f.hearingRecordings AS hr WHERE f.name = ?1 AND hr.deleted = false")
    List<Folder> findByName(String folderName);

}
