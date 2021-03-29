package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;

import java.util.Arrays;
import java.util.HashSet;
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

    @Override
    public final Optional<Long> checkIfCaseExists(final String recordingReference) {

        Optional<HearingRecording> existingRecording =
            hearingRecordingRepository.findByRecordingReference(recordingReference);
        if (existingRecording.isPresent()) {
            return Optional.of(existingRecording.get().getCcdCaseId());
        }
        return Optional.empty();
    }

    @Override
    public HearingRecording persistRecording(final HearingRecordingDto hearingRecordingDto, final Long caseId) {
        HearingRecordingSegment segment = HearingRecordingSegment.builder()
            .ingestionFileSourceUri(hearingRecordingDto.getRecordingFileUri())
            .recordingLengthMins(hearingRecordingDto.getRecordingLength())
            .recordingSegment(hearingRecordingDto.getRecordingSegment())
            .build();

        HearingRecording hearingRecording = HearingRecording.builder()
            .recordingReference(hearingRecordingDto.getRecordingReference())
            .segments(new HashSet<HearingRecordingSegment>(Arrays.asList(segment)))
            .ccdCaseId(caseId)
            .build();
        return hearingRecordingRepository.save(hearingRecording);
    }
}
