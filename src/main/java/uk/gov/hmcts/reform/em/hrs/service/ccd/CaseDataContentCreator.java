package uk.gov.hmcts.reform.em.hrs.service.ccd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.ccd.model.CaseDocument;
import uk.gov.hmcts.reform.em.hrs.service.ccd.model.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.service.ccd.model.RecordingSegment;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

@Service
public class CaseDataContentCreator {

    private final ObjectMapper objectMapper;

    public CaseDataContentCreator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode createCaseStartData(final HearingRecordingDto hearingRecordingDto) {

        HearingRecording recording = HearingRecording.builder()
            .recordingFiles(new HashSet(Arrays.asList(createSegmentNode(hearingRecordingDto))))
            .recordingTime(LocalDateTime.now())
            .recordingTimeOfDay("morning")
            .hearingSource(hearingRecordingDto.getHearingSource().toString())
            .hearingLocation(hearingRecordingDto.getHearingLocation())
            .serviceCode(hearingRecordingDto.getServiceCode())
            .jurisdictionCode(hearingRecordingDto.getJurisdictionCode())
            .courtLocationCode(hearingRecordingDto.getCourtLocationCode())
            .recordingReference(hearingRecordingDto.getRecordingReference())
            .build();
        return objectMapper.convertValue(recording, JsonNode.class);
    }

    public Map<String, Object> createCaseUpdateData(final Map<String, Object> caseData,
                                                    final HearingRecordingDto hearingRecordingDto) {

        ArrayNode existingSegments = caseData.get("recordingFiles") != null
            ? (ArrayNode) caseData.get("recordingFiles") : JsonNodeFactory.instance.arrayNode();

        RecordingSegment segment = createSegmentNode(hearingRecordingDto);

        existingSegments.add(objectMapper.convertValue(segment, JsonNode.class));

        return caseData;
    }

    private RecordingSegment createSegmentNode(HearingRecordingDto hearingRecordingDto) {
        CaseDocument recordingFile = CaseDocument.builder()
            .filename(hearingRecordingDto.getRecordingFilename())
            .url(hearingRecordingDto.getRecordingFileUri())
            .binaryUrl(hearingRecordingDto.getRecordingFileUri() + "/binary")
            .build();

        RecordingSegment segment = RecordingSegment.builder()
            .recordingFile(recordingFile)
            .segmentNumber(0)
            .recordingLength(10)
            .build();

        return segment;
    }
}
