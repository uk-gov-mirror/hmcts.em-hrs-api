package uk.gov.hmcts.reform.em.hrs.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;
import uk.gov.hmcts.reform.em.hrs.service.MonthlyReportService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class MonthlyHearingReportService extends MonthlyReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonthlyHearingReportService.class);

    private static final String SUBJECT_PREFIX = "Monthly hearing report for ";

    private static final String ATTACHMENT_PREFIX = "Monthly-hearing-report-";

    private final HearingRecordingSegmentRepository hearingRecordingSegmentRepository;
    private final HearingReportCsvWriter hearingReportCsvWriter;

    public MonthlyHearingReportService(
        HearingRecordingSegmentRepository hearingRecordingSegmentRepository,
        HearingReportCsvWriter hearingReportCsvWriter
    ) {
        this.hearingRecordingSegmentRepository = hearingRecordingSegmentRepository;
        this.hearingReportCsvWriter = hearingReportCsvWriter;
    }

    @Override
    protected File generateCsvReport(LocalDateTime startOfMonth, LocalDateTime endOfMonth) throws IOException {
        var recordings = hearingRecordingSegmentRepository
            .findByCreatedOnBetweenDatesWithHearingRecording(startOfMonth, endOfMonth);
        return hearingReportCsvWriter.writeHearingRecordingSummaryToCsv(recordings);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String getSubjectPrefix() {
        return SUBJECT_PREFIX;
    }

    @Override
    public String getAttachmentPrefix() {
        return ATTACHMENT_PREFIX;
    }


}
