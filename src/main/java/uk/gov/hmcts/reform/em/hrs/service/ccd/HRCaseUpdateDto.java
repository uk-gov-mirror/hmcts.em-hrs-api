package uk.gov.hmcts.reform.em.hrs.service.ccd;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class HRCaseUpdateDto {

    private final JsonNode ccdPayload;

    public HRCaseUpdateDto(JsonNode ccdPayload) throws IOException {
        this.ccdPayload = ccdPayload;
        if (ccdPayload == null) {
            throw new CantReadCcdPayloadException("Payload from CCD is empty");
        }
    }

    public JsonNode getCaseData() {
        return ccdPayload != null && ccdPayload.findValue("case_data") != null
            ? ccdPayload.findValue("case_data") : null;
    }

    public String getCaseId() {
        return ccdPayload != null && ccdPayload.findValue("id") != null
            ? ccdPayload.findValue("id").asText() : null;
    }

    public String getJurisdiction() {
        return ccdPayload != null && ccdPayload.findValue("jurisdiction") != null
            ? ccdPayload.findValue("jurisdiction").asText() : null;
    }

    public String getCaseTypeId() {
        return ccdPayload != null && ccdPayload.findValue("case_type_id") != null
            ? ccdPayload.findValue("case_type_id").asText() : null;
    }

    public String getEventToken() {
        return ccdPayload != null && ccdPayload.findValue("token") != null
            ? ccdPayload.findValue("token").asText() : null;
    }

    public String getEventId() {
        return ccdPayload != null && ccdPayload.findValue("event_id") != null
            ? ccdPayload.findValue("event_id").asText() : null;
    }
}
