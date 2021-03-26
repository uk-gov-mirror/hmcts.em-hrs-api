package uk.gov.hmcts.reform.em.hrs.service;

import java.util.UUID;

public interface ShareService {

    void executeNotify(UUID recordingId, String emailAddress);
}
