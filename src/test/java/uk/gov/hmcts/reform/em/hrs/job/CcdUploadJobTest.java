package uk.gov.hmcts.reform.em.hrs.job;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionContext;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.ccd.CcdUploadService;
import uk.gov.hmcts.reform.em.hrs.util.CcdUploadQueue;

import java.util.concurrent.RejectedExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.CASE_REFERENCE;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.INGESTION_QUEUE_SIZE;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.RECORDING_DATETIME;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.RECORDING_REFERENCE;

class CcdUploadJobTest {
    private static final HearingRecordingDto HEARING_RECORDING_DTO = HearingRecordingDto.builder()
        .caseRef(CASE_REFERENCE)
        .recordingSource("CVP")
        .courtLocationCode("LC")
        .jurisdictionCode("JC")
        .hearingRoomRef("123")
        .recordingRef(RECORDING_REFERENCE)
        .filename("hearing-recording-file-name")
        .recordingDateTime(RECORDING_DATETIME)
        .filenameExtension("mp4")
        .fileSize(123456789L)
        .segment(0)
        .cvpFileUrl("recording-cvp-uri")
        .checkSum("erI2foA30B==")
        .build();
    private final CcdUploadQueue ccdUploadQueue = CcdUploadQueue.builder()
        .capacity(INGESTION_QUEUE_SIZE)
        .build();
    private final CcdUploadService ccdUploadService = mock(CcdUploadService.class);
    private final JobExecutionContext context = mock(JobExecutionContext.class);
    private final CcdUploadJob underTest = new CcdUploadJob(ccdUploadQueue, ccdUploadService);

    @BeforeEach
    void prepare() {
        ccdUploadQueue.clear();
    }

    @Test
    void testShouldInvokeuploadionServiceWhenHearingRecordingIsPolled() {
        ccdUploadQueue.offer(HEARING_RECORDING_DTO);
        doNothing().when(ccdUploadService).upload(HEARING_RECORDING_DTO);

        underTest.executeInternal(context);

        verify(ccdUploadService, times(1)).upload(HEARING_RECORDING_DTO);
    }

    @Test
    void testShouldNotInvokeuploadionServiceWhenNullIsPolled() {
        doNothing().when(ccdUploadService).upload(any(HearingRecordingDto.class));

        underTest.executeInternal(context);

        verify(ccdUploadService, never()).upload(any(HearingRecordingDto.class));
    }

    //TODO discuss with team...is this actually testing correctly...its ensuring code coverage is exercised and that
    // no exceptions are
    //thrown during exception handling, but it doesn't actually test the queue being full....
    @Test
    void testShouldHandleGracefullyWhenAysncQueueIsFull() {
        ccdUploadQueue.offer(HEARING_RECORDING_DTO);
        doThrow(RejectedExecutionException.class).when(ccdUploadService).upload(any(HearingRecordingDto.class));
        underTest.executeInternal(context);
        verify(ccdUploadService, times(1)).upload(any(HearingRecordingDto.class));
    }

    @Test
    void testShouldHandleGracefullyWhenUnhandledError() {
        ccdUploadQueue.offer(HEARING_RECORDING_DTO);
        doThrow(RuntimeException.class).when(ccdUploadService).upload(any(HearingRecordingDto.class));
        underTest.executeInternal(context);
        verify(ccdUploadService, times(1)).upload(any(HearingRecordingDto.class));
    }


}
