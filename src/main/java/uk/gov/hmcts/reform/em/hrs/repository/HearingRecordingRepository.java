package uk.gov.hmcts.reform.em.hrs.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HearingRecordingRepository extends PagingAndSortingRepository<HearingRecording, UUID> {

    Optional<HearingRecording> findByCcdCaseId(Long ccdCaseId);
}
