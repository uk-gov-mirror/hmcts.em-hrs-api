package uk.gov.hmcts.reform.em.hrs.controller;

import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil;
import uk.gov.hmcts.reform.em.hrs.service.FolderService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingSegmentService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingShareesService;
import uk.gov.hmcts.reform.em.hrs.service.ShareService;

import java.util.Set;
import javax.inject.Inject;

import static java.util.Collections.emptySet;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class HearingRecordingControllerTest {
    @Inject
    private MockMvc mockMvc;

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

    @Test
    void testWhenRequestedFolderDoesNotExistOrIsEmpty() throws Exception {
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
}
