package uk.gov.hmcts.reform.em.hrs.controller;

import net.javacrumbs.jsonunit.core.Option;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.em.hrs.Application;
import uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil;
import uk.gov.hmcts.reform.em.hrs.service.FolderService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingSegmentService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingShareesService;
import uk.gov.hmcts.reform.em.hrs.service.ShareService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import static java.util.Collections.emptySet;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Import(TestSecurityConfiguration.class)
public class HearingRecordingControllerTest extends BaseTest {

    @MockBean
    private FolderService folderService;

    @MockBean
    private HearingRecordingService hearingRecordingService;

    @MockBean
    private HearingRecordingSegmentService hearingRecordingSegmentService;

    @MockBean
    private HearingRecordingShareesService hearingRecordingShareesService;

    @MockBean
    private ShareService shareService;

    private static final String TEST_FOLDER = "folder-1";
    private static final UUID ID = TestUtil.HEARING_RECORDING.getId();

    @Test
    public void testWhenRequestedFolderDoesNotExistOrIsEmpty() throws Exception {
        final String path = "/folders/" + TEST_FOLDER + "/hearing-recording-file-names";
        doReturn(emptySet()).when(folderService).getStoredFiles(TEST_FOLDER);

        final MvcResult mvcResult = mockMvc.perform(get(path).accept(APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_VALUE))
            .andReturn();

        final String content = mvcResult.getResponse().getContentAsString();

        assertThatJson(content)
            .when(Option.IGNORING_ARRAY_ORDER)
            .and(
                x -> x.node("folder-name").isEqualTo("folder-1"),
                x -> x.node("filenames").isArray().isEmpty()
            );
        verify(folderService, times(1)).getStoredFiles(TEST_FOLDER);
    }

    @Test
    void testWhenRequestedFolderHasStoredFiles() throws Exception {
        final String path = "/folders/" + TEST_FOLDER + "/hearing-recording-file-names";
        doReturn(Set.of(TestUtil.FILE_1, TestUtil.FILE_2)).when(folderService).getStoredFiles(TEST_FOLDER);

        final MvcResult mvcResult = mockMvc.perform(get(path).accept(APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_VALUE))
            .andReturn();

        final String content = mvcResult.getResponse().getContentAsString();

        assertThatJson(content)
            .when(Option.IGNORING_ARRAY_ORDER)
            .and(
                x -> x.node("folder-name").isEqualTo(TEST_FOLDER),
                x -> x.node("filenames").isArray().isEqualTo(json("[\"file-1.mp4\",\"file-2.mp4\"]"))
            );
        verify(folderService, times(1)).getStoredFiles(TEST_FOLDER);
    }

    @Test
    public void testShareHearingRecording() throws Exception {
        final String path = "/folders/" + TEST_FOLDER + "/hearing-recording/" + ID + "/access-right";

        doReturn(Optional.of(TestUtil.HEARING_RECORDING)).when(hearingRecordingService).findOne(ID);

        mockMvc.perform(post(path)
                            .contentType(APPLICATION_JSON_VALUE)
                            .header("Authorization", "xxx")
                            .header("ServiceAuthorization", "xxx"))
            .andExpect(status().isOk());

        verify(shareService, times(1))
            .executeNotify(TestUtil.HEARING_RECORDING, any(HttpServletRequest.class));

    }

    @Test
    public void testShareHearingRecordingNotFound() throws Exception {
        final String path = "/folders/" + TEST_FOLDER + "/hearing-recording/" + ID + "/access-right";

        mockMvc.perform(post(path)
                            .contentType(APPLICATION_JSON_VALUE)
                            .header("Authorization", "xxx")
                            .header("ServiceAuthorization", "xxx"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testShareHearingRecordingBadRequest() throws Exception {
        final String path = "/folders/" + TEST_FOLDER + "/hearing-recording/" + ID + "/access-right";

        doReturn(Optional.of(TestUtil.HEARING_RECORDING)).when(hearingRecordingService).findOne(ID);
        doReturn(ResponseEntity.badRequest().build())
            .when(shareService).executeNotify(TestUtil.HEARING_RECORDING, Mockito.any(HttpServletRequest.class));

        mockMvc.perform(post(path)
                            .contentType(APPLICATION_JSON_VALUE)
                            .header("Authorization", "xxx")
                            .header("ServiceAuthorization", "xxx"))
            .andExpect(status().isBadRequest());

    }

}
