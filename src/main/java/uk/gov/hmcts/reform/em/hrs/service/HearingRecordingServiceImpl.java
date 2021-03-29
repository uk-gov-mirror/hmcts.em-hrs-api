package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.exception.HearingRecordingNotFoundException;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;
import uk.gov.hmcts.reform.em.hrs.util.Tuple2;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class HearingRecordingServiceImpl implements HearingRecordingService {
    private final HearingRecordingRepository hearingRecordingRepository;
    private final String emailDomain;

    @Inject
    public HearingRecordingServiceImpl(final HearingRecordingRepository hearingRecordingRepository,
                                       final @Value("${notify.email.domain}") String emailDomain) {
        this.hearingRecordingRepository = hearingRecordingRepository;
        this.emailDomain = emailDomain;
    }

    @Override
    public Tuple2<HearingRecording, Set<String>> getDownloadSegmentUris(final Long ccdCaseId) {
        final Optional<HearingRecording> hearingRecording = hearingRecordingRepository.findByCcdCaseId(ccdCaseId);

        final Set<String> downloadLinks = hearingRecording
            .map(x -> buildDownloadLinks(x.getSegments()))
            .orElseThrow(() -> new HearingRecordingNotFoundException(ccdCaseId));

        return new Tuple2<>(hearingRecording.get(), downloadLinks);
    }

    private Set<String> buildDownloadLinks(Set<HearingRecordingSegment> segments) {
        return segments.stream()
            .map(x -> String.format("%s/%s", emailDomain, x.getFileName()))
            .collect(Collectors.toUnmodifiableSet());
    }
}
