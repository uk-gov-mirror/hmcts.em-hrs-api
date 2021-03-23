package uk.gov.hmcts.reform.em.hrs.service.ccd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.ccd.model.CaseDocument;
import uk.gov.hmcts.reform.em.hrs.service.ccd.model.CaseHearingRecording;
import uk.gov.hmcts.reform.em.hrs.service.ccd.model.RecordingSegment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CaseDataContentCreator {

    private final ObjectMapper objectMapper;

    public CaseDataContentCreator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode createCaseStartData(final HearingRecordingDto hearingRecordingDto) {

        CaseHearingRecording recording = CaseHearingRecording.builder()
            .recordingFiles(Arrays.asList(createSegment(hearingRecordingDto)))
            .recordingDateTime(hearingRecordingDto.getRecordingDateTime())
            .recordingTimeOfDay("morning")
            .hearingSource(hearingRecordingDto.getHearingSource().toString())
            .hearingRoomRef(hearingRecordingDto.getHearingRoomRef())
            .serviceCode(hearingRecordingDto.getServiceCode())
            .jurisdictionCode(hearingRecordingDto.getJurisdictionCode())
            .courtLocationCode(hearingRecordingDto.getCourtLocationCode())
            .recordingReference(hearingRecordingDto.getRecordingReference())
            .build();
        return objectMapper.convertValue(recording, JsonNode.class);
    }

    public Map<String, Object> createCaseUpdateData(final Map<String, Object> caseData,
                                                    final HearingRecordingDto hearingRecordingDto) {
        @SuppressWarnings("unchecked")
        List<RecordingSegment> segments = (ArrayList) caseData.getOrDefault("recordingFiles", new ArrayList());
        segments.add(createSegment(hearingRecordingDto));
        return caseData;
    }

    private RecordingSegment createSegment(HearingRecordingDto hearingRecordingDto) {
        CaseDocument recordingFile = CaseDocument.builder()
            .filename(hearingRecordingDto.getRecordingFilename())
            .url(hearingRecordingDto.getRecordingFileUri())
            .binaryUrl(hearingRecordingDto.getRecordingFileUri() + "/binary")
            .build();

        return RecordingSegment.builder()
            .recordingFile(recordingFile)
            .segmentNumber(hearingRecordingDto.getRecordingSegment())
            .recordingLength(hearingRecordingDto.getRecordingLength())
            .build();
    }
}
