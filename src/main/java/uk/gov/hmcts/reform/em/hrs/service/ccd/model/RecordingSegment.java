package uk.gov.hmcts.reform.em.hrs.service.ccd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordingSegment {

    @JsonProperty("documentLink")
    private CaseDocument recordingFile;

    @JsonProperty("segmentNumber")
    private Integer segmentNumber;

    @JsonProperty("recordingLength")
    private Integer recordingLength;
}
