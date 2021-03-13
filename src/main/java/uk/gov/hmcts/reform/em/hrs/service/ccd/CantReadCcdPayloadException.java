package uk.gov.hmcts.reform.em.hrs.service.ccd;

public class CantReadCcdPayloadException extends RuntimeException {
    public CantReadCcdPayloadException(String payload_from_ccd_is_empty) {
    }
}
