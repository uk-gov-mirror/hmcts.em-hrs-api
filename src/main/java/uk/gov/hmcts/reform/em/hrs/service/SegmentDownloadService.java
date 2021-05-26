package uk.gov.hmcts.reform.em.hrs.service;

import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SegmentDownloadService {

    Map<String, String> getDownloadInfo(UUID recordingId, Integer segmentNo);

    void download(String filename, HttpServletRequest request, HttpServletResponse response);
}
