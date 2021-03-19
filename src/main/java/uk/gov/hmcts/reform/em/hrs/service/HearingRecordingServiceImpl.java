package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;
import java.util.UUID;

@Named
public class HearingRecordingServiceImpl implements HearingRecordingService {
    private final HearingRecordingRepository hearingRecordingRepository;

    @Inject
    public HearingRecordingServiceImpl(final HearingRecordingRepository hearingRecordingRepository) {
        this.hearingRecordingRepository = hearingRecordingRepository;
    }

    public Optional<HearingRecording> findOne(UUID id) {
        Optional<HearingRecording> hearingRecording = hearingRecordingRepository.findById(id);
        return hearingRecording;
    }
}
