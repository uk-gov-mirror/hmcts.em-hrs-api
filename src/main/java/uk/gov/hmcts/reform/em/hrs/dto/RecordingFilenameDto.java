package uk.gov.hmcts.reform.em.hrs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecordingFilenameDto {
    private final String folderName;
    private final List<String> filenames;
}
