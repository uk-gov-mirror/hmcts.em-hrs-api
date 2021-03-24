package uk.gov.hmcts.reform.em.hrs.service;

public interface ShareService {
    void executeNotify(final Long ccdCaseId, final String recipientEmailAddress, final String authorizationJwt);
}
