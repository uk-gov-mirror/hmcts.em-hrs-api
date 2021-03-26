package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSharee;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingShareesRepository;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@Transactional
public class HearingRecordingShareeServiceImpl implements HearingRecordingShareeService {
    private final HearingRecordingShareesRepository hearingRecordingShareesRepository;

    @Inject
    public HearingRecordingShareeServiceImpl(
        final HearingRecordingShareesRepository hearingRecordingShareesRepository) {
        this.hearingRecordingShareesRepository = hearingRecordingShareesRepository;
    }

    public HearingRecordingSharee createAndSaveEntry(final String emailAddress,
                                                     final HearingRecording hearingRecording) {
        final HearingRecordingSharee hearingRecordingSharee = HearingRecordingSharee.builder()
            .hearingRecording(hearingRecording)
            .shareeEmail(emailAddress).build();
        hearingRecordingShareesRepository.save(hearingRecordingSharee);
        return hearingRecordingSharee;
    }

}
