package uk.gov.hmcts.reform.em.hrs.service.ccd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseDocument {

    @JsonProperty("document_url")
    private String url;

    @JsonProperty("document_binary_url")
    private String binaryUrl;

    @JsonProperty("document_filename")
    private String filename;
}
