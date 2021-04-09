package uk.gov.hmcts.reform.em.hrs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class HearingRecordingDto {

    private final String caseRef;
    private final String recordingSource;
    private final String hearingRoomRef;
    private final String serviceCode;
    private final String jurisdictionCode;
    private final String courtLocationCode;
    private final String recordingRef;
    private final String cvpFileUrl;
    private final String filename;
    private final String filenameExtension;
    private final Long fileSize;
    private final int segment;
    private final String checkSum;
    @JsonFormat(pattern = "yyyy-MM-dd-HH.mm.ss.SSS")
    private final LocalDateTime recordingDateTime;
}
