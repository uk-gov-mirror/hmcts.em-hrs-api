package uk.gov.hmcts.reform.em.hrs.service.ccd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.model.CaseDocument;
import uk.gov.hmcts.reform.em.hrs.model.CaseHearingRecording;
import uk.gov.hmcts.reform.em.hrs.model.CaseRecordingFile;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class CaseDataContentCreator {

    private final ObjectMapper objectMapper;

    public CaseDataContentCreator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode createCaseStartData(final HearingRecordingDto hearingRecordingDto, final UUID recordingId) {

        CaseHearingRecording recording = CaseHearingRecording.builder()
            .recordingFiles(Collections.singletonList(createSegment(hearingRecordingDto, recordingId)))
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

    public Map<String, Object> createCaseUpdateData(final Map<String, Object> caseData, final UUID recordingId,
                                                    final HearingRecordingDto hearingRecordingDto) {
        @SuppressWarnings("unchecked")
        List<Map> segmentNodes = (ArrayList) caseData.putIfAbsent("recordingFiles", new ArrayList());

        boolean segmentAlreadyAdded = segmentNodes.stream()
            .map(segmentNode -> objectMapper.convertValue(segmentNode.get("value"), CaseRecordingFile.class))
            .map(segment -> segment.getCaseDocument())
            .anyMatch(recordingFile -> recordingFile.getFilename().equals(hearingRecordingDto.getFilename()));

        if (!segmentAlreadyAdded) {
            segmentNodes.add(createSegment(hearingRecordingDto, recordingId));
        }
        return caseData;
    }

    private Map<String, CaseRecordingFile> createSegment(HearingRecordingDto hearingRecordingDto, UUID recordingId) {

        String documentUrl = String.format("%s/hearing-recordings/%s/segments/%d",
                                           hearingRecordingDto.getUrlDomain(), recordingId,
                                           hearingRecordingDto.getSegment());

        CaseDocument recordingFile = CaseDocument.builder()
            .filename(hearingRecordingDto.getFilename())
            .url(documentUrl)
            .binaryUrl(documentUrl)
            .build();

        return Map.of("value", CaseRecordingFile.builder()
            .caseDocument(recordingFile)
            .segmentNumber(hearingRecordingDto.getSegment())
            .fileSize(hearingRecordingDto.getFileSize())
            .build());
    }

    private String getTimeOfDay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.getHour() < 12 ? "AM" : "PM" : null;
    }
}
