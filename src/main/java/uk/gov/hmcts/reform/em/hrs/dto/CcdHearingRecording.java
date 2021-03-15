package uk.gov.hmcts.reform.em.hrs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class CcdHearingRecording {

    @JsonProperty("caseHearingSource")
    private Enum<HearingSource> hearingSource;

    @JsonProperty("caseFileCaseID")
    private String caseId;

    @JsonProperty("caseHearingLocation")
    private String hearingLocation;

    @JsonProperty("caseRecordingDate")
    private Date recordingDate;

    @JsonProperty("caseRecordingTimeOfDay")
    private String recordingTimeOfDay;

    @JsonProperty("caseServiceCode")
    private String serviceCode;

    @JsonProperty("caseJurisdictionCode")
    private String jurisdictionCode;

    @JsonProperty("caseCourtLocationCode")
    private String courtLocationCode;

    @JsonProperty("caseRecordingReference")
    private String recordingReference;

    @JsonProperty("caseCreatedDate")
    private String createdDate;

    @JsonProperty("caseAudioFiles")
    private ArrayNode recordingSegments;

}
