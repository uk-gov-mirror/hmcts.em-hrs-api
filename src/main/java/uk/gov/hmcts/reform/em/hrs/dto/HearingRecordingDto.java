package uk.gov.hmcts.reform.em.hrs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HearingRecordingDto {

    private String caseRef;
    private String recordingSource;
    private String hearingRoomRef;
    private String serviceCode;
    private String jurisdictionCode;
    private String courtLocationCode;
    private String recordingRef;
    private String cvpFileUrl;
    private String filename;
    private String filenameExtension;
    private Long fileSize;
    private int segment;
    private String checkSum;

    @JsonFormat(pattern = "yyyy-MM-dd-HH.mm.ss.SSS")
    @DateTimeFormat(pattern = "yyyy-MM-dd-HH.mm.ss.SSS")
    private LocalDateTime recordingDateTime;

}
