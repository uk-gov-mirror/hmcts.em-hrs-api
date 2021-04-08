package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.scheduling.annotation.Async;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.ccd.CaseUpdateService;
import uk.gov.hmcts.reform.em.hrs.util.Snooper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class IngestionServiceImpl implements IngestionService {
    private final HearingRecordingService recordingService;
    private final HearingRecordingSegmentService segmentService;
    private final CaseUpdateService caseUpdateService;
    private final Snooper snooper;

    @Inject
    public IngestionServiceImpl(final HearingRecordingService recordingService,
                                final HearingRecordingSegmentService segmentService,
                                final CaseUpdateService caseUpdateService,
                                final Snooper snooper) {
        this.recordingService = recordingService;
        this.segmentService = segmentService;
        this.caseUpdateService = caseUpdateService;
        this.snooper = snooper;
    }

    @Override
    @Async("HrsAsyncExecutor")
    public void ingest(final HearingRecordingDto hearingRecordingDto) {
        final CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            final Optional<HearingRecording> hearingRecording =
                recordingService.findByRecordingRef(hearingRecordingDto.getRecordingRef());

            final Long caseId = caseUpdateService.addRecordingToCase(
                hearingRecordingDto,
                hearingRecording.map(HearingRecording::getCcdCaseId)
            );
            // TODO: Should not persist into database if record failed to create CCD record
            segmentService.persistRecording(hearingRecordingDto, hearingRecording, caseId);
        });
        /*final CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            LOGGER.info("Task::Beautiful started.");
            try {
                TimeUnit.SECONDS.sleep(3);
                snooper.snoop("Beautiful");
            } catch (InterruptedException e) {
                snooper.snoop(e.getMessage());
            }
            LOGGER.info("Task::Beautiful stopped.");
        });
        final CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            LOGGER.info("Task::World started.");
            try {
                TimeUnit.SECONDS.sleep(5);
                snooper.snoop("World");
            } catch (InterruptedException e) {
                snooper.snoop(e.getMessage());
            }
            LOGGER.info("Task::World stopped.");
        });*/

        try {
            CompletableFuture
                .allOf(future1/*, future2, future3*/)
                .get();
        } catch (InterruptedException | ExecutionException e) {
            snooper.snoop("Something broke", e);
        }
    }
}
