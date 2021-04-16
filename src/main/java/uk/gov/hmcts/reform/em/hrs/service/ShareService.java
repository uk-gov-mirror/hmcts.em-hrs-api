package uk.gov.hmcts.reform.em.hrs.service;

public interface ShareService {
    void executeNotify(final Long ccdCaseId, final String shareeEmailAddress, final String authorizationToken);
}
