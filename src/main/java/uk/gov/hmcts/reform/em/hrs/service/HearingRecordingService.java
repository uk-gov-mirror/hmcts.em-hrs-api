package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.util.Tuple2;

import java.util.Set;

public interface HearingRecordingService {

    Tuple2<HearingRecording, Set<String>> getDownloadSegmentUris(Long ccdId);

}
