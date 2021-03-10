package uk.gov.hmcts.reform.em.hrs.service.ccd;

import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.em.hrs.dto.RecordingFilenameDto;

public class CcdClient {

    private static final String CASE_TYPE = "NEED_TO_FIND_CASE_TYPE";
    private static final String JURISDICTION = "NEED_TO_FIND_JURISDICTION";
    private static final String EVENT_ID = "createCase";

    //private final IdamHelper idamHelper;
    //private final S2sHelper s2sHelper;
    private final CoreCaseDataApi coreCaseDataApi;

    public CcdClient(CoreCaseDataApi coreCaseDataApi) {
        //this.idamHelper = idamHelper;
        //this.s2sHelper = s2sHelper;
        this.coreCaseDataApi = coreCaseDataApi;
    }

    /***
     * Create new case with new recording file
     * @param recordingFile
     * @return
     */
    public Long createHRCase(RecordingFilenameDto recordingFile) {
        final String userAuthorization = "userToken";//idamHelper.authenticateUser(username);
        final String s2sAuthorization = "s2sToken";//s2sHelper.getS2sToken();

        StartEventResponse startEventResponse = coreCaseDataApi.startCase(
            userAuthorization,
            s2sAuthorization,
            CASE_TYPE,
            EVENT_ID);

        Object caseData = "NEED TO CONSTRUCT CASE DATA";

        CaseDetails caseDetails = coreCaseDataApi.submitForCaseworker(
            userAuthorization,
            s2sAuthorization,
            "userId",//idamHelper.getUserId(username),
            JURISDICTION,
            CASE_TYPE,
            false,
            CaseDataContent.builder()
                .event(Event.builder().id(startEventResponse.getEventId()).build())
                .eventToken(startEventResponse.getToken())
                .data(caseData).build());

        return caseDetails.getId();
    }

    /***
     * Update case with new recordingFile
     * @param caseId
     * @param recordingFile
     */
    public void updateHRCase(Long caseId, RecordingFilenameDto recordingFile) {


    }
}
