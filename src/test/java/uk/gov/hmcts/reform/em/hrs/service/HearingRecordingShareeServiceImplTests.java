package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSharee;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingShareesRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.HEARING_RECORDING;

@ExtendWith(MockitoExtension.class)
public class HearingRecordingShareeServiceImplTests {
    @Mock
    private HearingRecordingShareesRepository hearingRecordingShareesRepository;

    @InjectMocks
    private HearingRecordingShareeServiceImpl hearingRecordingShareesServiceImpl;


    @Test
    public void testShouldSaveEntity() {
        final String emailAddress = "testerEmail@test.com";

        hearingRecordingShareesServiceImpl.createAndSaveEntry(emailAddress, HEARING_RECORDING);

        verify(hearingRecordingShareesRepository, times(1)).save(any(HearingRecordingSharee.class));
    }
}
