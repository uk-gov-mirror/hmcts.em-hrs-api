package uk.gov.hmcts.reform.em.hrs.service.ccd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.tokens.SecurityClient;

import java.util.Map;

@Service
public class CcdDataStoreApiClient {

    private final Logger log = LoggerFactory.getLogger(CcdDataStoreApiClient.class);

    private final SecurityClient securityClient;
    private final CaseDataContentCreator caseDataCreator;
    private final CoreCaseDataApi coreCaseDataApi;

    private static final String JURISDICTION = "HRS";
    private static final String CASE_TYPE = "HearingRecordings";
    private static final String CREATE_CASE = "createCase";
    private static final String ADD_RECORDING_FILE = "manageFiles";

    public CcdDataStoreApiClient(SecurityClient securityClient,
                                 CaseDataContentCreator caseDataCreator,
                                 CoreCaseDataApi coreCaseDataApi) {
        this.securityClient = securityClient;
        this.caseDataCreator = caseDataCreator;
        this.coreCaseDataApi = coreCaseDataApi;
    }

    public CaseDetails createCase(final HearingRecordingDto hearingRecordingDto) {
        Map<String, String> tokens = securityClient.getTokens();

        StartEventResponse startEventResponse =
            coreCaseDataApi.startCase(tokens.get("user"), tokens.get("service"), CASE_TYPE, CREATE_CASE);

        CaseDataContent caseData = CaseDataContent.builder()
            .event(Event.builder().id(startEventResponse.getEventId()).build())
            .eventToken(startEventResponse.getToken())
            .data(caseDataCreator.createCaseStartData(hearingRecordingDto))
            .build();

        CaseDetails caseDetails = coreCaseDataApi
            .submitForCaseworker(tokens.get("user"), tokens.get("service"), tokens.get("userId"),
                                 JURISDICTION, CASE_TYPE, false, caseData);

        return caseDetails;
    }

    public CaseDetails updateCaseData(final String caseId, final HearingRecordingDto hearingRecordingDto) {
        Map<String, String> tokens = securityClient.getTokens();

        StartEventResponse startEventResponse =
            coreCaseDataApi.startEvent(tokens.get("user"), tokens.get("service"), caseId, ADD_RECORDING_FILE);

        CaseDataContent caseData = CaseDataContent.builder()
            .event(Event.builder().id(startEventResponse.getEventId()).build())
            .eventToken(startEventResponse.getToken())
            .data(caseDataCreator.createCaseUpdateData(
                startEventResponse.getCaseDetails().getData(),
                hearingRecordingDto)
            ).build();

        return coreCaseDataApi.submitEventForCaseWorker(tokens.get("user"), tokens.get("service"), tokens.get("userId"),
                                                        JURISDICTION, CASE_TYPE, caseId, false, caseData);
    }
}
