package uk.gov.hmcts.reform.em.hrs.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import uk.gov.hmcts.reform.em.hrs.domain.JobInProgress;

import java.util.List;
import java.util.UUID;

public interface JobInProgressRepository extends PagingAndSortingRepository<JobInProgress, UUID> {
    List<JobInProgress> findByFolder(String folderName);
}
