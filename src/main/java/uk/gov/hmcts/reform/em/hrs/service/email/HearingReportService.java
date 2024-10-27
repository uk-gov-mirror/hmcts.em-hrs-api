package uk.gov.hmcts.reform.em.hrs.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

@Service
public class HearingReportService implements MonthlyReportContentCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(HearingReportService.class);

    private static final String SUBJECT_PREFIX = "Monthly hearing report for ";

    private static final String ATTACHMENT_PREFIX = "Monthly-hearing-report-";

    private final HearingRecordingSegmentRepository hearingRecordingSegmentRepository;
    private final HearingReportCsvWriter hearingReportCsvWriter;

    public HearingReportService(
        HearingRecordingSegmentRepository hearingRecordingSegmentRepository,
        HearingReportCsvWriter hearingReportCsvWriter
    ) {
        this.hearingRecordingSegmentRepository = hearingRecordingSegmentRepository;
        this.hearingReportCsvWriter = hearingReportCsvWriter;
    }

    public File createMonthlyReport(Month month, int year) throws IOException {
        LocalDateTime startOfMonth = getStartOfMonth(month, year);
        LocalDateTime endOfMonth = getEndOfMonth(month, year);
        LOGGER.info("get records for from: {},to:{}", startOfMonth, endOfMonth);
        var list = hearingRecordingSegmentRepository
            .findByCreatedOnBetweenDatesWithHearingRecording(startOfMonth, endOfMonth);
        return hearingReportCsvWriter.writeHearingRecordingSummaryToCsv(list);
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
