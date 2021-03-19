package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSharees;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingShareesRepository;

import javax.inject.Inject;
import javax.inject.Named;


@Named
@Service
@Transactional
public class HearingRecordingShareesServiceImpl implements HearingRecordingShareesService {
    private final HearingRecordingShareesRepository hearingRecordingShareesRepository;

    @Inject
    public HearingRecordingShareesServiceImpl(final HearingRecordingShareesRepository hearingRecordingShareesRepository)
    {
        this.hearingRecordingShareesRepository = hearingRecordingShareesRepository;
    }


    public HearingRecordingSharees createAndSaveEntry(String emailAddress, HearingRecording hearingRecording) {
        HearingRecordingSharees hearingRecordingSharees = new HearingRecordingSharees();
        hearingRecordingSharees.setHearingRecording(hearingRecording);
        hearingRecordingSharees.setShareeEmail(emailAddress);
        hearingRecordingShareesRepository.save(hearingRecordingSharees);
        return hearingRecordingSharees;
    }

}
