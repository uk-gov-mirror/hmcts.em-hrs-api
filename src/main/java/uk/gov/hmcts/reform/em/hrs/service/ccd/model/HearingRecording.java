package uk.gov.hmcts.reform.em.hrs.service.ccd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class HearingRecording {

    @JsonProperty("hearingSource")
    private String hearingSource;

    @JsonProperty("hearingLocation")
    private String hearingLocation;

    @JsonProperty("recordingTimeOfDay")
    private String recordingTimeOfDay;

    @JsonProperty("recordingTime")
    private LocalDateTime recordingTime;

    @JsonProperty("serviceCode")
    private String serviceCode;

    @JsonProperty("jurisdictionCode")
    private String jurisdictionCode;

    @JsonProperty("courtLocationCode")
    private String courtLocationCode;

    @JsonProperty("recordingReference")
    private String recordingReference;

    @JsonProperty("recordingFiles")
    private Set<RecordingSegment> recordingFiles;

    /***
     * Format recording dateTime to satisfy CCD validation requirements
     * @return CCD-compliant recording dateTime
     */
    public String getRecordingTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return formatter.format(recordingTime);
    }

    /***
     * Add value property to collection entries to satisfy CCD validation requirements
     * @return CCD-compliant recordingFiles collection
     */
    public Set getRecordingFiles() {
        return recordingFiles.stream()
                .map(file -> JsonNodeFactory.instance.objectNode()
                        .set("value", JsonNodeFactory.instance.pojoNode(file)))
                .collect(Collectors.toSet());
    }
}
