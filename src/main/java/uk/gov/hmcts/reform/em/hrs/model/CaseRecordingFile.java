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
    private CaseDocument caseDocument;

    @JsonProperty("segmentNumber")
    private Integer segmentNumber;

    @JsonProperty("fileSize")
    private Long fileSize;
}
