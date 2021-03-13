package uk.gov.hmcts.reform.em.hrs.service.ccd;

import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.em.hrs.dto.RecordingFilenameDto;
import uk.gov.hmcts.reform.em.hrs.service.tokens.IdamHelper;
import uk.gov.hmcts.reform.em.hrs.service.tokens.S2sHelper;

public class CcdClient {

    private static final String EVENT_ID = "createCase";
    private static final String CASE_TYPE = "NEED_TO_FIND_CASE_TYPE";//TODO - GET CASE TYPE
    private static final String JURISDICTION = "NEED_TO_FIND_JURISDICTION";//TODO - GET JURISDICTION

    private final IdamHelper idamHelper;
    private final S2sHelper s2sHelper;
    private final CoreCaseDataApi coreCaseDataApi;
    private final CcdDataStoreApiClient ccdDataStoreApiClient;
    private final CaseDataContentCreator caseDataContentCreator;

    public CcdClient(CoreCaseDataApi coreCaseDataApi,
                     CcdDataStoreApiClient ccdDataStoreApiClient,
                     CaseDataContentCreator caseDataContentCreator,
                     IdamHelper idamHelper, S2sHelper s2sHelper) {
        this.idamHelper = idamHelper;
        this.s2sHelper = s2sHelper;
        this.coreCaseDataApi = coreCaseDataApi;
        this.caseDataContentCreator = caseDataContentCreator;
        this.ccdDataStoreApiClient = ccdDataStoreApiClient;
    }

    /***
     * Create new case with new recording file
     * @param recordingFile
     * @return
     */
    public Long createHRCase(RecordingFilenameDto recordingFile) {
        final String userAuthorization = idamHelper.getUserToken();
        final String userId = idamHelper.getUserId();
        final String s2sAuthorization = s2sHelper.getS2sToken();

        StartEventResponse startEventResponse =
            coreCaseDataApi.startCase(userAuthorization, s2sAuthorization, CASE_TYPE, EVENT_ID);

        CaseDetails caseDetails = coreCaseDataApi
            .submitForCaseworker(userAuthorization, s2sAuthorization, userId,
                                 JURISDICTION, CASE_TYPE, false,
                                 caseDataContentCreator.createStartCaseDataContent(startEventResponse, recordingFile));

        return caseDetails.getId();
    }

    /***
     * Update case with new recordingFile
     * @param caseId
     * @param recordingFilenameDto
     */
    public void updateHRCase(String caseId, RecordingFilenameDto recordingFilenameDto) {
        HRCaseUpdateDto hrCaseUpdateDto = null;
        final String userAuthorization = idamHelper.getUserToken();
        try {
            hrCaseUpdateDto = ccdDataStoreApiClient.getHRCaseData(caseId, userAuthorization);
        } finally {
            if (hrCaseUpdateDto != null) {
                ccdDataStoreApiClient.updateHRCaseData(hrCaseUpdateDto, userAuthorization, recordingFilenameDto);
            }
        }
    }
}
