package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSharees;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingShareesRepository;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HearingRecordingShareesServiceImplTests {
    @Mock
    private HearingRecordingShareesRepository hearingRecordingShareesRepository;

    @InjectMocks
    private HearingRecordingShareesServiceImpl hearingRecordingShareesServiceImpl;


    @Test
    public void testCreateAndSaveEntity() {

        //  Create a HearingRecord
        UUID uuid = UUID.randomUUID();
        HearingRecording hearingRecording = new HearingRecording();
        hearingRecording.setId(uuid);
        hearingRecording.setCreatedBy("tester");

        String emailAddress = "testerEmail@test.com";

        // Save into the hearingRecordingShareesService
        hearingRecordingShareesServiceImpl.createAndSaveEntry(emailAddress, hearingRecording);

        // verify the hearingRecordingShareesRepository is being called correctly
        verify(hearingRecordingShareesRepository, times(1)).save(any(HearingRecordingSharees.class));
    }
}
