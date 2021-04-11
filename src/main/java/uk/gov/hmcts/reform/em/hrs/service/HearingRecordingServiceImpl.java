package uk.gov.hmcts.reform.em.hrs.service;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.exception.HearingRecordingNotFoundException;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class HearingRecordingServiceImpl implements HearingRecordingService {

    private final HearingRecordingRepository hearingRecordingRepository;

    @Inject
    public HearingRecordingServiceImpl(final HearingRecordingRepository hearingRecordingRepository) {
        this.hearingRecordingRepository = hearingRecordingRepository;
    }

    @Override
    public Tuple2<HearingRecording, Set<String>> getDownloadSegmentUris(final Long ccdCaseId) {
        final Optional<HearingRecording> hearingRecording = hearingRecordingRepository.findByCcdCaseId(ccdCaseId);

        return hearingRecording
            .map(x -> {
                final Set<String> links = buildDownloadLinks(x.getSegments());

                return Tuples.of(x, links);
            })
            .orElseThrow(() -> new HearingRecordingNotFoundException(ccdCaseId));
    }

    private Set<String> buildDownloadLinks(Set<HearingRecordingSegment> segments) {
        return segments.stream()
            .map(x -> x.getFilename()) // TODO: this is a placeholder.  Field not yet available
            .collect(Collectors.toUnmodifiableSet());
    }

}
