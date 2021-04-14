package uk.gov.hmcts.reform.em.hrs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseRecordingFile {

    @JsonProperty("documentLink")
    private CaseDocument recordingFile;

    @JsonProperty("segmentNumber")
    private Integer segmentNumber;

    @JsonProperty("fileSize")
    private Long fileSize;

    /**
     * Add value property to segment to satisfy CCD validation requirements.
     * @return CCD-compliant recordingSegment
    */
    public JsonNode getValue() {
        ObjectNode segmentValue = JsonNodeFactory.instance.objectNode();
        segmentValue.set("documentLink", JsonNodeFactory.instance.pojoNode(recordingFile));
        segmentValue.put("segmentNumber", segmentNumber);
        segmentValue.put("fileSize", fileSize);
        return segmentValue;
    }
}
