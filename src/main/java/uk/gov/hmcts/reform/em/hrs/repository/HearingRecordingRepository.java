package uk.gov.hmcts.reform.em.hrs.repository;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
//import uk.gov.hmcts.reform.em.hrs.commandobject.MetadataSearchCommand;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HearingRecordingRepository extends PagingAndSortingRepository<HearingRecording, UUID> {

//    @Query("select s from StoredDocument s join s.metadata m where s.deleted = false and KEY(m) = :#{#metadataSearchCommand.name} and m = :#{#metadataSearchCommand.value}")
//    Page<StoredDocument> findAllByMetadata(@NonNull @Param("metadataSearchCommand") MetadataSearchCommand metadataSearchCommand, @NonNull Pageable pageable);


    @Query("select s from HearingRecording s where s.deleted = false and s.createdBy = :#{#creator}")
    Page<HearingRecording> findByCreatedBy(@Param("creator") String creator, @NonNull Pageable pageable);

    List<HearingRecording> findByTtlLessThanAndHardDeleted(Date date, Boolean hardDeleted);

    Optional<HearingRecording> findByIdAndDeleted(UUID uuid, boolean deleted);

}
