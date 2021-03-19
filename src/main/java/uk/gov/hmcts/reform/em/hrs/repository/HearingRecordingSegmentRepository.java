package uk.gov.hmcts.reform.em.hrs.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;

import java.util.List;
import java.util.UUID;


@Repository
public interface HearingRecordingSegmentRepository extends PagingAndSortingRepository<HearingRecordingSegment, UUID> {

    @Query("select hrs from HearingRecordingSegment hrs where hrs.hearingRecording.id = :#{#recordingId}")
    List<HearingRecordingSegment> findByRecordingId(@Param("recordingId") UUID recordingId);

}
