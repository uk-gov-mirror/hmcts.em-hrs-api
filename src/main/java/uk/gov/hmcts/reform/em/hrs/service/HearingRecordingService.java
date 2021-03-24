package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.util.Tuple2;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface HearingRecordingService {

    Tuple2<HearingRecording, Set<String>> getDownloadSegmentUris(Long ccdId);

    Optional<HearingRecording> findByRecordingRef(final String recordingReference);

    Optional<HearingRecording> findByCaseId(final Long caseId);

    HearingRecording createAndSaveEntry(HearingRecording hearingRecording);
}
