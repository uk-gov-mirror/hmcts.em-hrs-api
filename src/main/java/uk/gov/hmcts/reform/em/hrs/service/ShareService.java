package uk.gov.hmcts.reform.em.hrs.service;

//import com.microsoft.applicationinsights.core.dependencies.google.api.Http;
//import org.apache.commons.lang3.BooleanUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
//import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
//import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
//import uk.gov.hmcts.reform.em.hrs.service.NotificationService;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;

@Service
public class ShareService {

    private final NotificationService notificationService;

    public ShareService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // TODO - Fill this out

//    public HearingRecordingDto executeNotify(List<HearingRecordingSegment> hearingRecordingSegmentList,
//    HttpServletRequest request) {
//        HearingRecordingDto hearingRecordingDto =
//        notificationService.sendEmailNotification();
//    }

//    notificationService.sendEmailNotification(
//    failureTemplateId,
//        dto.getJwt(),
//        dto.getCaseId(),
//        dto.getCaseData().has("caseTitle") ? dto.getCaseData().get("caseTitle").asText() : "Bundle",
//        ccdCallbackResponseDto.getErrors().toString()
//            );
}
