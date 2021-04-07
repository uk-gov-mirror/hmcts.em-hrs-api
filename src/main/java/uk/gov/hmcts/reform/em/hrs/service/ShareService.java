package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.service.notify.NotificationClientException;

public interface ShareService {
    void executeNotify(final Long ccdCaseId, final String shareeEmailAddress, final String authorizationToken)
        throws NotificationClientException;
}
