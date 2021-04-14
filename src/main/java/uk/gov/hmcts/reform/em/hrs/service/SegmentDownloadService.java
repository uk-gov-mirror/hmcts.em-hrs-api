package uk.gov.hmcts.reform.em.hrs.service;

import java.io.OutputStream;

public interface SegmentDownloadService {

    void download(Long caseId, Integer segment, OutputStream outputStream);
}
