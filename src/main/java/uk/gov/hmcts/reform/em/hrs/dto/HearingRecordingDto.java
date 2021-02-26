package uk.gov.hmcts.reform.em.hrs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HearingRecordingDto {
    private final String recordingFileUri;
    private final String checkSum;
}
