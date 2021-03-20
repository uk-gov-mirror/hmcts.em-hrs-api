package uk.gov.hmcts.reform.em.hrs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Getter
@AllArgsConstructor
public class HearingRecordingDto {
    private final String recordingFileUri;
    private final String recordingFilename;
    private final String checkSum;
    private final String caseRef;
    private Enum<HearingSource> hearingSource;
    private String caseId;
    private String hearingLocation;
    private Date recordingDate;
    private String recordingTimeOfDay;
    private String serviceCode;
    private String jurisdictionCode;
    private String courtLocationCode;
    private String recordingReference;
    private String createdDate;
    private int recordingSegment;
}
