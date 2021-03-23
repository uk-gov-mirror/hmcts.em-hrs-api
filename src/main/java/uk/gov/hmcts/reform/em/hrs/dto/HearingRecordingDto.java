package uk.gov.hmcts.reform.em.hrs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HearingRecordingDto {

    private final String caseRef;
    private final String hearingSource;
    private final String hearingRoomRef;
    private final String serviceCode;
    private final String jurisdictionCode;
    private final String courtLocationCode;
    private final String recordingReference;
    private final String recordingFileUri;
    private final String recordingFilename;
    private final int recordingLength;
    private final int recordingSegment;
    private final String checkSum;
    private final LocalDateTime recordingDateTime;
}
