package uk.gov.hmcts.reform.em.hrs.service.ccd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.model.CaseDocument;
import uk.gov.hmcts.reform.em.hrs.model.CaseHearingRecording;
import uk.gov.hmcts.reform.em.hrs.model.RecordingSegment;

import java.util.ArrayList;
import java.util.Collections;
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
            .recordingFiles(Collections.singletonList(createSegment(hearingRecordingDto)))
            .recordingDateTime(hearingRecordingDto.getRecordingDateTime())
            .recordingTimeOfDay("morning") // TODO set correct time of day
            .hearingSource(hearingRecordingDto.getRecordingSource())
            .hearingRoomRef(hearingRecordingDto.getHearingRoomRef())
            .serviceCode(hearingRecordingDto.getServiceCode())
            .jurisdictionCode(hearingRecordingDto.getJurisdictionCode())
            .courtLocationCode(hearingRecordingDto.getCourtLocationCode())
            .recordingReference(hearingRecordingDto.getRecordingRef())
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

        //force the target url to be acceptable by CCD data API
        //this is forced to be the domain as specfied in the ccd dependencies file under CCD_DM_DOMAIN: http://dm-store:8080
        String tempUrlFixer = "http://dm-store:8080/documents/hrs-will-be-fixed";
        CaseDocument recordingFile = CaseDocument.builder()
            .filename(hearingRecordingDto.getFilename())
            .url(tempUrlFixer)//TODO: this is CVP url, I need to construct it from filename
            .binaryUrl(tempUrlFixer + "/binary")
            .build();

        return RecordingSegment.builder()
            .recordingFile(recordingFile)
            .segmentNumber(hearingRecordingDto.getSegment())
            .fileSize(hearingRecordingDto.getFileSize())
            .build();
    }
}
