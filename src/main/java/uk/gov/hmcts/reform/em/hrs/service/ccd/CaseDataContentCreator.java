package uk.gov.hmcts.reform.em.hrs.service.ccd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.model.CaseDocument;
import uk.gov.hmcts.reform.em.hrs.model.CaseHearingRecording;
import uk.gov.hmcts.reform.em.hrs.model.CaseRecordingFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class CaseDataContentCreator {

    private final ObjectMapper objectMapper;

    @Value("${app.url}")
    private String applicationUrl;

    public CaseDataContentCreator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode createCaseStartData(final HearingRecordingDto hearingRecordingDto, Long caseId) {

        CaseHearingRecording recording = CaseHearingRecording.builder()
            .recordingFiles(Collections.singletonList(createSegment(hearingRecordingDto, caseId)))
            .recordingDateTime(hearingRecordingDto.getRecordingDateTime())
            .recordingTimeOfDay(getTimeOfDay(hearingRecordingDto.getRecordingDateTime()))
            .hearingSource(hearingRecordingDto.getRecordingSource())
            .hearingRoomRef(hearingRecordingDto.getHearingRoomRef())
            .serviceCode(hearingRecordingDto.getServiceCode())
            .jurisdictionCode(hearingRecordingDto.getJurisdictionCode())
            .courtLocationCode(hearingRecordingDto.getCourtLocationCode())
            .recordingReference(hearingRecordingDto.getRecordingRef())
            .build();
        return objectMapper.convertValue(recording, JsonNode.class);
    }

    public Map<String, Object> createCaseUpdateData(final Map<String, Object> caseData, final Long caseId,
                                                    final HearingRecordingDto hearingRecordingDto) {
        @SuppressWarnings("unchecked")
        List<CaseRecordingFile> segments = (ArrayList) caseData.getOrDefault("recordingFiles", new ArrayList());
        boolean segmentAlreadyAdded = segments.stream()
            .map(segment -> segment.getRecordingFile().getFilename())
            .anyMatch(filename -> filename.equals(hearingRecordingDto.getFilename()));

        if (!segmentAlreadyAdded) {
            segments.add(createSegment(hearingRecordingDto, caseId));
        }
        return caseData;
    }

    private CaseRecordingFile createSegment(HearingRecordingDto hearingRecordingDto, Long caseId) {

        String documentPath = String.format("{}/hearing-recordings/{}/segments/{}",
                                           applicationUrl, caseId, hearingRecordingDto.getSegment());

        String documentUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
            .replacePath(documentPath)
            .build()
            .toUriString();

        CaseDocument recordingFile = CaseDocument.builder()
            .filename(hearingRecordingDto.getFilename())
            .url(documentUrl)
            .binaryUrl(documentUrl)
            .build();

        return CaseRecordingFile.builder()
            .recordingFile(recordingFile)
            .segmentNumber(hearingRecordingDto.getSegment())
            .fileSize(hearingRecordingDto.getFileSize())
            .build();
    }

    private String getTimeOfDay(LocalDateTime dateTime) {
        return dateTime.getHour() < 12 ? "AM" : "PM";
    }
}
