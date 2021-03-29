package uk.gov.hmcts.reform.em.hrs.componenttests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.em.hrs.domain.Folder;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.domain.JobInProgress;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TestUtil {
    public static final String FILE_1 = "file-1.mp4";
    public static final String FILE_2 = "file-2.mp4";
    public static final String FILE_3 = "file-3.mp4";
    public static final String TEST_FOLDER_NAME = "folder-1";
    public static final UUID RANDOM_UUID = UUID.randomUUID();
    public static final Long CCD_CASE_ID = 1234L;
    public static final String RECIPIENT_EMAIL_ADDRESS = "testerEmail@test.com";
    private static final String EMAIL_DOMAIN = "https://SOMEPREFIXTBD";

    private static final HearingRecordingSegment SEGMENT_1 = HearingRecordingSegment.builder()
        .id(RANDOM_UUID)
        .filename(FILE_1)
        .build();
    private static final HearingRecordingSegment SEGMENT_2 = HearingRecordingSegment.builder()
        .id(RANDOM_UUID)
        .filename(FILE_2)
        .build();
    private static final HearingRecordingSegment SEGMENT_3 = HearingRecordingSegment.builder()
        .id(RANDOM_UUID)
        .filename(FILE_3)
        .build();

    public static final String BLOB_DATA = "data";
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
        MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(),
        StandardCharsets.UTF_8
    );

    public static final Set<String> SEGMENTS_DOWNLOAD_LINKS = Set.of(
        String.format("%s/%s", EMAIL_DOMAIN, SEGMENT_1.getFileName()),
        String.format("%s/%s", EMAIL_DOMAIN, SEGMENT_2.getFileName()),
        String.format("%s/%s", EMAIL_DOMAIN, SEGMENT_3.getFileName())
    );

    public static final Folder EMPTY_FOLDER = Folder.builder()
        .id(RANDOM_UUID)
        .name("name")
        .hearingRecordings(Collections.emptyList())
        .jobsInProgress(Collections.emptyList())
        .build();

    public static final Folder FOLDER = Folder.builder()
        .id(RANDOM_UUID)
        .hearingRecordings(List.of(HearingRecording.builder()
                                       .id(RANDOM_UUID)
                                       .segments(Collections.emptySet())
                                       .build()))
        .jobsInProgress(Collections.emptyList())
        .build();

    public static final Folder FOLDER_WITH_SEGMENT = Folder.builder()
        .id(RANDOM_UUID)
        .hearingRecordings(List.of(HearingRecording.builder()
                                       .id(RANDOM_UUID)
                                       .segments(Set.of(SEGMENT_1, SEGMENT_2, SEGMENT_3))
                                       .build()))
        .jobsInProgress(Collections.emptyList())
        .build();

    public static final Folder FOLDER_WITH_JOBS_IN_PROGRESS = Folder.builder()
        .id(RANDOM_UUID)
        .hearingRecordings(Collections.emptyList())
        .jobsInProgress(List.of(
            JobInProgress.builder().filename(FILE_1).build(),
            JobInProgress.builder().filename(FILE_2).build()
        ))
        .build();

    public static final Folder FOLDER_WITH_SEGMENT_AND_IN_PROGRESS = Folder.builder()
        .id(RANDOM_UUID)
        .hearingRecordings(List.of(HearingRecording.builder()
                                       .id(RANDOM_UUID)
                                       .segments(Set.of(SEGMENT_1, SEGMENT_2))
                                       .build()))
        .jobsInProgress(List.of(JobInProgress.builder().filename(FILE_3).build()))
        .build();

    public static final HearingRecording HEARING_RECORDING = HearingRecording.builder()
        .id(RANDOM_UUID)
        .folder(Folder.builder().id(RANDOM_UUID).build())
        .build();

    public static final HearingRecording HEARING_RECORDING_WITH_SEGMENTS = HearingRecording.builder()
        .id(RANDOM_UUID)
        .segments(Set.of(SEGMENT_1, SEGMENT_2, SEGMENT_3))
        .folder(Folder.builder().id(RANDOM_UUID).build())
        .build();

    private TestUtil() {
    }

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper om = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return om.writeValueAsBytes(object);
    }

    public static String convertObjectToJsonString(Object object) throws IOException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }
}
