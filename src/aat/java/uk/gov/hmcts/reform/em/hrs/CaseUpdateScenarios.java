package uk.gov.hmcts.reform.em.hrs;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CaseUpdateScenarios extends BaseTest {

    private static final String JURISDICTION = "HRS";
    private static final String CASE_TYPE = "HearingRecordings";
    private static final String SHARE_FILES = "shareFiles";
    private static final String FOLDER = "audiostream02";
    private static final String RECORDING_REF = "audiostream02/audio_test.m4a";

    @Autowired
    protected ExtendedCcdHelper extendedCcdHelper;

    @Autowired
    private CoreCaseDataApi coreCaseDataApi;

    @Test
    public void testCcdCaseUpdate() {

        s2sAuthRequest()
            .relaxedHTTPSValidation()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .when()
            .get(String.format("/folders/%s", FOLDER))
            .then()
            .statusCode(200);

        JsonNode reqBody = extendedCcdHelper.createRecordingSegment(
            FOLDER,
            String.format("http://localhost:10000/devstoreaccount1/cvptestcontainer/%s", RECORDING_REF),
            RECORDING_REF,
            "ma4",
            0
            );

        s2sAuthRequest()
            .relaxedHTTPSValidation()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .body(reqBody)
            .when()
            .post("/segments")
            .then()
            .statusCode(202);
    }

    @Ignore
    @Test
    public void testDocumentShare() {
        Map<String, String> tokens = extendedCcdHelper.getTokens();
        Map<String, String> searchCriteria = Map.of("case.recordingReference", RECORDING_REF);
        Long caseId = coreCaseDataApi
            .searchForCaseworker(tokens.get("user"), tokens.get("service"), tokens.get("userId"),
                                 JURISDICTION, CASE_TYPE, searchCriteria)
            .stream().findAny()
            .map(caseDetails -> caseDetails.getId())
            .orElse(1619005282012509L);

        StartEventResponse startEventResponse = coreCaseDataApi.startEvent(tokens.get("user"), tokens.get("service"),
                                                                           caseId.toString(), SHARE_FILES
        );

        CaseDataContent caseData = CaseDataContent.builder()
            .event(Event.builder().id(startEventResponse.getEventId()).build())
            .eventToken(startEventResponse.getToken())
            .data(extendedCcdHelper.getShareRequest()).build();

        coreCaseDataApi
            .submitEventForCaseWorker(tokens.get("user"), tokens.get("service"), tokens.get("userId"),
                                      JURISDICTION, CASE_TYPE, caseId.toString(), false, caseData);
    }
}
