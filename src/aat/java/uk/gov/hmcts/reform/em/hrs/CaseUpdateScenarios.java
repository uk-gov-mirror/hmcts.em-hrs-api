package uk.gov.hmcts.reform.em.hrs;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CaseUpdateScenarios extends BaseTest {

    private static final String JURISDICTION = "HRS";
    private static final String CASE_TYPE = "HearingRecordings";
    private static final String SHARE_FILES = "shareFiles";
    private static final String FOLDER = "audiostream999000";
    private static final String RECORDING_REF = "audiostream999000/FM-0111-testfile200M_2020-01-01-11.11.11.123-UTC_0.mp4";

    @Autowired
    protected ExtendedCcdHelper extendedCcdHelper;

    @Autowired
    private CoreCaseDataApi coreCaseDataApi;

    @Value("${azure.storage.cvp.container-url}")
    private String cvpContainerUrl;

    @Test
    public void testCcdCaseUpdate() throws InterruptedException {

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
            cvpContainerUrl + RECORDING_REF,
            RECORDING_REF,
            "mp4",
            0,
            "2020-01-01-11.11.11.123"
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

        Thread.sleep(60000L);

        List<CaseDetails> results = extendedCcdHelper.searchForCase(RECORDING_REF);
        Assertions.assertFalse(results.isEmpty());
    }

    @Ignore
    @Test
    public void testDocumentShare() {
        Map<String, String> tokens = extendedCcdHelper.getTokens();
        Long caseId = extendedCcdHelper.searchForCase(RECORDING_REF)
            .stream().findFirst()
            .map(caseDetails -> caseDetails.getId())
            .orElse(1619005282012509L);

        StartEventResponse startEventResponse = coreCaseDataApi.startEvent(tokens.get("user"), tokens.get("service"),
                                                                           caseId.toString(), SHARE_FILES);

        CaseDataContent caseData = CaseDataContent.builder()
            .event(Event.builder().id(startEventResponse.getEventId()).build())
            .eventToken(startEventResponse.getToken())
            .data(extendedCcdHelper.getShareRequest()).build();

        coreCaseDataApi
            .submitEventForCaseWorker(tokens.get("user"), tokens.get("service"), tokens.get("userId"),
                                      JURISDICTION, CASE_TYPE, caseId.toString(), false, caseData);
    }
}
