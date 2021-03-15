package uk.gov.hmcts.reform.em.hrs.service.ccd;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.em.hrs.dto.CcdHearingRecording;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;

import java.util.Map;

@Service
public class CaseDataContentCreator {

    public CaseDataContent createStartCaseDataContent(StartEventResponse startEventResponse,
                                                       HearingRecordingDto hearingRecordingDto) {

        ObjectNode segment = createSegmentNode(hearingRecordingDto);

        CcdHearingRecording hearingRecording = new CcdHearingRecording(
            hearingRecordingDto.getHearingSource(),
            hearingRecordingDto.getCaseId(),
            hearingRecordingDto.getHearingLocation(),
            hearingRecordingDto.getRecordingDate(),
            hearingRecordingDto.getRecordingTimeOfDay(),
            hearingRecordingDto.getServiceCode(),
            hearingRecordingDto.getJurisdictionCode(),
            hearingRecordingDto.getCourtLocationCode(),
            hearingRecordingDto.getRecordingReference(),
            hearingRecordingDto.getCreatedDate(),
            JsonNodeFactory.instance.arrayNode().add(String.valueOf(segment))
        );
        return CaseDataContent.builder()
            .event(Event.builder().id(startEventResponse.getEventId()).build())
            .eventToken(startEventResponse.getToken())
            .data(hearingRecording)
            .build();
    }
    public CaseDataContent createUpdateCaseDataContent(StartEventResponse startEventResponse,
                                                       HearingRecordingDto hearingRecordingDto) {

        Map caseData = startEventResponse.getCaseDetails().getData();
        ArrayNode existingSegments = caseData.get("caseAudioFiles") != null ? (ArrayNode) caseData.get("caseAudioFiles")
            : JsonNodeFactory.instance.arrayNode();

        existingSegments.add(createSegmentNode(hearingRecordingDto));

        return CaseDataContent.builder()
            .event(Event.builder().id(startEventResponse.getEventId()).build())
            .eventToken(startEventResponse.getToken())
            .data(caseData)
            .build();
    }

    private ObjectNode createSegmentNode(HearingRecordingDto hearingRecordingDto) {
        ObjectNode documentLink = JsonNodeFactory.instance.objectNode();
        documentLink.put("url", hearingRecordingDto.getRecordingFileUri());
        documentLink.put("binaryUrl", hearingRecordingDto.getRecordingFileUri());
        documentLink.put("filename", hearingRecordingDto.getRecordingReference());

        ObjectNode segment = JsonNodeFactory.instance.objectNode();
        segment.put("documentLink", documentLink);
        segment.put("audioFileSegment", hearingRecordingDto.getRecordingSegment());
        segment.put("audioFileLength", hearingRecordingDto.getRecordingSegment());
        return segment;
    }
}
