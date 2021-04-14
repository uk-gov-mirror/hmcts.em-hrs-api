package uk.gov.hmcts.reform.em.hrs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseHearingRecording {

    @JsonProperty("hearingSource")
    private String hearingSource;

    @JsonProperty("hearingRoomRef")
    private String hearingRoomRef;

    @JsonProperty("recordingDateTime")
    private LocalDateTime recordingDateTime;

    @JsonProperty("recordingTimeOfDay")
    private String recordingTimeOfDay;

    @JsonProperty("serviceCode")
    private String serviceCode;

    @JsonProperty("jurisdictionCode")
    private String jurisdictionCode;

    @JsonProperty("courtLocationCode")
    private String courtLocationCode;

    @JsonProperty("recordingReference")
    private String recordingReference;

    @JsonProperty("recordingFiles")
    private List<CaseRecordingFile> recordingFiles;

    /**
     * Format recording dateTime to satisfy CCD validation requirements.
     * @return CCD-compliant recording dateTime
    */
    public String getRecordingDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return formatter.format(recordingDateTime);
    }
}
