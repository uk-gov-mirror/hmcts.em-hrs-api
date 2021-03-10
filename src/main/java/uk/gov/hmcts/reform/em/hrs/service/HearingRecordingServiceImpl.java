package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;

import java.util.Optional;
import java.util.UUID;
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
    public Optional<HearingRecording> findOne(UUID id) {
        Optional<HearingRecording> hearingRecording = hearingRecordingRepository.findById(id);
        return hearingRecording;
    }

    @Override
    public HearingRecording createAndSaveEntry(HearingRecording hearingRecording) {
        hearingRecordingRepository.save(hearingRecording);
        return hearingRecording;
    }



    public final Optional<Long> checkIfHRCaseAlredyCreated(String caseRef) {

        Optional<HearingRecording> existingRecording = hearingRecordingRepository.findByCaseReference(caseRef);
        if (existingRecording.isPresent()) {
            return Optional.of(existingRecording.get().getCcdCaseId());
        }
        return Optional.empty();
    }
}
