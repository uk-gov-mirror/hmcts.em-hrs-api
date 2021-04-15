package uk.gov.hmcts.reform.em.hrs.service.ccd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.model.CaseHearingRecording;
import uk.gov.hmcts.reform.em.hrs.service.SecurityService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;


@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({MockitoExtension.class})
class CcdDataStoreApiClientTest {

    @Mock
    private SecurityService securityService;

    @Mock
    private CaseDataContentCreator caseDataCreator;

    @Mock
    private CoreCaseDataApi coreCaseDataApi;

    @InjectMocks
    private CcdDataStoreApiClient underTest;

    private static final String CASE_TYPE = "HearingRecordings";
    private static final String CREATE_CASE = "createCase";
    private static final String JURISDICTION = "HRS";
    private static final String ADD_RECORDING_FILE = "manageFiles";

    @Test
    void createCase_positive_test() {

        Map map = Map.of("user", "xxx",
                         "service", "yyy",
                         "userId", "aaa"
        );
        StartEventResponse startEventResponse = StartEventResponse
            .builder()
            .token("TOKEN")
            .eventId("EVENT-ID")
            .build();
        HearingRecordingDto hearingRecordingDto = new HearingRecordingDto();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        hearingRecordingDto.setRecordingDateTime(now);

        CaseHearingRecording recording = CaseHearingRecording.builder()
            .recordingDateTime(now)
            .recordingTimeOfDay("morning").build();
        CaseDetails caseDetails = CaseDetails.builder().id(1213456789L).build();

        doReturn(map).when(securityService).getTokens();
        doReturn(startEventResponse).when(coreCaseDataApi).startCase(
            "xxx",
            "yyy",
            CASE_TYPE,
            CREATE_CASE
        );
        JsonNode jsonNode = new ObjectMapper().convertValue(recording, JsonNode.class);
        doReturn(jsonNode)
            .when(caseDataCreator).createCaseStartData(any());
        doReturn(caseDetails).when(coreCaseDataApi).submitForCaseworker(
            eq("xxx"),
            eq("yyy"),
            eq("aaa"),
            eq(JURISDICTION),
            eq(CASE_TYPE),
            eq(false),
            any()
        );
        Long id = underTest.createCase(hearingRecordingDto);
        assertEquals(1213456789L, id);

    }

    @Test
    @Disabled("TODO - Mockito Mocking not working...")
    void updateCaseData_positive_test() {

        Map map = Map.of("user", "xxx", "service", "yyy",
                         "userId", "aaa"
        );
        CaseDetails caseDetails = CaseDetails.builder().id(123456789L).data(new HashMap<String, Object>()).build();
        StartEventResponse startEventResponse = StartEventResponse
            .builder()
            .token("TOKEN")
            .eventId("EVENT-ID")
            .caseDetails(caseDetails)
            .build();
        HearingRecordingDto hearingRecordingDto = new HearingRecordingDto();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        hearingRecordingDto.setRecordingDateTime(now);

        Mockito.lenient().doReturn(map).when(securityService).getTokens();
        Mockito.lenient().doReturn(startEventResponse).when(coreCaseDataApi).startEvent(
            "xxx",
            "yyy",
            "123456789",
            ADD_RECORDING_FILE
        );
        Mockito.lenient().doReturn(caseDetails).when(coreCaseDataApi).submitEventForCaseWorker(
            eq("xxx"),
            eq("yyy"),
            eq("aaa"),
            eq(JURISDICTION),
            eq(CASE_TYPE),
            eq("123456789"),
            eq(false),
            any()
        );
        Long id = underTest.updateCaseData(123456789L, hearingRecordingDto);
        assertEquals(123456789L,id);
    }

    @AfterEach
    void reset() {
        //Mockito.reset();
    }
}
