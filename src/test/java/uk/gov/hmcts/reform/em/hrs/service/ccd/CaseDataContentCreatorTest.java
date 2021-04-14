package uk.gov.hmcts.reform.em.hrs.service.ccd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CaseDataContentCreatorTest {

    CaseDataContentCreator underTest;

    @BeforeAll
    void init() {
        underTest = new CaseDataContentCreator(new ObjectMapper());
    }

    @Test
    void createCaseStartData_null_parameter_test() {
        NullPointerException npe = assertThrows(
            NullPointerException.class,
            () -> {
                underTest.createCaseStartData(null);
            }
        );
        assertEquals(
            "hearingRecordingDto is marked non-null but is null",
            npe.getMessage()
        );
    }

    @Test
    void createCaseUpdateData_empty_parameter_test() {
        HearingRecordingDto hearingRecordingDto = new HearingRecordingDto();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        hearingRecordingDto.setRecordingDateTime(now);
        //TODO - Clarify the Following points...
        //recordingDateTime - Should be made mandatory...?As without this the Object Creation is failing.
        //Why do we have 2 recordingFiles Created by Default????
        //Why is the Morning populated as default for the recordingTimeOfDay
        //Date Format Not working as expected....
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        JsonNode jsonNode = underTest.createCaseStartData(hearingRecordingDto);
        hearingRecordingDto.setRecordingDateTime(now);
        assertEquals("morning", jsonNode.get("recordingTimeOfDay").asText()); //Get the only element in the root node
        assertEquals(now.format(formatter),
                     jsonNode.get("recordingDateTime").asText()); //Get the only element in the root node
    }

    @Test
    void createCaseUpdateData_populated_parameter_test() {
        HearingRecordingDto hearingRecordingDto = new HearingRecordingDto();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        hearingRecordingDto.setRecordingDateTime(now);
        hearingRecordingDto.setRecordingDateTime(now);
        hearingRecordingDto.setFilename("TEST-FILENAME");
        hearingRecordingDto.setSegment(100);
        hearingRecordingDto.setFileSize(99999999999L);
        hearingRecordingDto.setHearingRoomRef("Ref");
        hearingRecordingDto.setRecordingSource("RecordingSource");
        hearingRecordingDto.setServiceCode("Service Code");
        hearingRecordingDto.setCourtLocationCode("300");
        hearingRecordingDto.setJurisdictionCode("KENT");
        hearingRecordingDto.setRecordingRef("Recording-Number-1");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        JsonNode jsonNode = underTest.createCaseStartData(hearingRecordingDto);
        System.out.println(jsonNode.toPrettyString());
        assertEquals("morning", jsonNode.get("recordingTimeOfDay").asText());
        assertEquals(now.format(formatter), jsonNode.get("recordingDateTime").asText());
        assertEquals("Ref", jsonNode.at("/hearingRoomRef").asText());
        assertEquals("RecordingSource", jsonNode.at("/hearingSource").asText());
        assertEquals("300", jsonNode.at("/courtLocationCode").asText());
        assertEquals("Service Code", jsonNode.at("/serviceCode").asText());
        assertEquals("KENT", jsonNode.at("/jurisdictionCode").asText());
        assertEquals("Recording-Number-1", jsonNode.at("/recordingReference").asText());
        //TODO - Verify the Recording Files
    }



}
