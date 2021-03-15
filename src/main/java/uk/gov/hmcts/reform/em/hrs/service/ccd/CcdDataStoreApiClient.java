package uk.gov.hmcts.reform.em.hrs.service.ccd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.tokens.IdamHelper;

@Service
public class CcdDataStoreApiClient {

    private final Logger log = LoggerFactory.getLogger(CcdDataStoreApiClient.class);

    private final IdamHelper idamHelper;
    private final AuthTokenGenerator authTokenGenerator;
    private final CaseDataContentCreator caseDataContentCreator;
    private final CoreCaseDataApi coreCaseDataApi;

    private static final String CREATE_CASE = "createCase";
    private static final String CASE_TYPE = "NEED_TO_FIND_CASE_TYPE";//TODO - GET CASE TYPE
    private static final String JURISDICTION = "NEED_TO_FIND_JURISDICTION";//TODO - GET JURISDICTION

    private final String ADD_RECORDING_FILE = "addRecordingFile";


    public CcdDataStoreApiClient(IdamHelper idamHelper,
                                 AuthTokenGenerator authTokenGenerator,
                                 CaseDataContentCreator caseDataContentCreator,
                                 CoreCaseDataApi coreCaseDataApi) {
        this.idamHelper = idamHelper;
        this.authTokenGenerator = authTokenGenerator;
        this.caseDataContentCreator = caseDataContentCreator;
        this.coreCaseDataApi = coreCaseDataApi;
    }

    public Long createHRCase(HearingRecordingDto hearingRecordingDto) {
        final String userToken = idamHelper.getUserToken();
        final String userId = idamHelper.getUserId(userToken);
        final String s2sToken = authTokenGenerator.generate();

        StartEventResponse startEventResponse =
            coreCaseDataApi.startCase(userToken, s2sToken, CASE_TYPE, CREATE_CASE);

        CaseDataContent caseData =
            caseDataContentCreator.createStartCaseDataContent(startEventResponse, hearingRecordingDto);

        CaseDetails caseDetails = coreCaseDataApi
            .submitForCaseworker(userToken, s2sToken, userId, JURISDICTION, CASE_TYPE, false, caseData);

        return caseDetails.getId();
    }

    public void updateHRCaseData(String caseId, HearingRecordingDto hearingRecordingDto) {
        final String userToken = idamHelper.getUserToken();
        final String userId = idamHelper.getUserId(userToken);
        final String s2sToken = authTokenGenerator.generate();

        StartEventResponse startEventResponse =
            coreCaseDataApi.startEvent(userToken, s2sToken, caseId, ADD_RECORDING_FILE);

        CaseDataContent caseData =
            caseDataContentCreator.createUpdateCaseDataContent(startEventResponse, hearingRecordingDto);

        coreCaseDataApi
            .submitForCaseworker(userToken, s2sToken, userId, JURISDICTION, CASE_TYPE, false, caseData);

    }
}
