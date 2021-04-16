package uk.gov.hmcts.reform.em.hrs.service;

import java.io.OutputStream;
import java.util.UUID;

public interface SegmentDownloadService {

    void download(UUID recordingId, Integer segment, OutputStream outputStream);
}
