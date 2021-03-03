package uk.gov.hmcts.reform.em.hrs.controller;

import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingService;

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
    private HearingRecordingService hearingRecordingService;

    private static final String TEST_FOLDER = "folder-1";

    @Test
    void testWhenRequestedFolderDoesNotExistOrIsEmpty() throws Exception {
        final String path = "/folders/" + TEST_FOLDER + "/hearing-recording-file-names";
        doReturn(emptySet()).when(hearingRecordingService).getStoredFiles(TEST_FOLDER);

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
        verify(hearingRecordingService, times(1)).getStoredFiles(TEST_FOLDER);
    }

    @Test
    void testWhenRequestedFolderHasStoredFiles() throws Exception {
        final String path = "/folders/" + TEST_FOLDER + "/hearing-recording-file-names";
        doReturn(Set.of("file-1.mp4", "file-2.mp4")).when(hearingRecordingService).getStoredFiles(TEST_FOLDER);

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
        verify(hearingRecordingService, times(1)).getStoredFiles(TEST_FOLDER);
    }
}
