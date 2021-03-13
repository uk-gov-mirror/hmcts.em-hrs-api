package uk.gov.hmcts.reform.em.hrs.service.ccd;

public class CcdUpdateException extends RuntimeException {
    public CcdUpdateException(int statusCode, String response, String error) {
    }
}
