package uk.gov.hmcts.reform.em.hrs.service;

import reactor.util.function.Tuple2;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;

import java.util.Set;

public interface HearingRecordingService {

    Tuple2<HearingRecording, Set<String>> getDownloadSegmentUris(Long ccdId);

}
