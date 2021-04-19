package uk.gov.hmcts.reform.em.hrs.service;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public interface SegmentDownloadService {

    void download(UUID recordingId, Integer segment, HttpServletResponse response);
}
