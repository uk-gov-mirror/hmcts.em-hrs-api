package uk.gov.hmcts.reform.em.hrs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.hrs.componenttests.AbstractBaseTest;
import uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.service.*;
import uk.gov.hmcts.reform.em.hrs.service.ccd.CaseUpdateService;

import java.util.*;

import static java.util.Collections.emptySet;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HearingRecordingControllerTest extends AbstractBaseTest {

    @MockBean
    private FolderService folderService;

    @MockBean
    private CaseUpdateService caseUpdateService;

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
    void testWhenRequestedFolderDoesNotExistOrIsEmpty() throws Exception {
        final String path = "/folders/" + TEST_FOLDER;
        doReturn(emptySet()).when(folderService).getStoredFiles(TEST_FOLDER);

        final MvcResult mvcResult = mockMvc.perform(get(path).accept(APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_VALUE))
            .andReturn();

        final String content = mvcResult.getResponse().getContentAsString();

        assertThatJson(content)
            .when(Option.IGNORING_ARRAY_ORDER)
            .and(
                x -> x.node("folder-name").isEqualTo(TEST_FOLDER),
                x -> x.node("filenames").isArray().isEmpty()
            );
        verify(folderService, times(1)).getStoredFiles(TEST_FOLDER);
    }

    @Test
    void testWhenRequestedFolderHasStoredFiles() throws Exception {
        final String path = "/folders/" + TEST_FOLDER;
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
    void testShareHearingRecordingShareeSave() throws Exception {
        final String path = "/sharees";
        final Long CASE_ID = 12L;
        final HearingRecording HEARING_RECORDING = TestUtil.FOLDER_WITH_SEGMENT.getHearingRecordings().get(0);

        doReturn(Optional.of(HEARING_RECORDING)).when(hearingRecordingService).findByCaseId(CASE_ID);

        Map<String, Object> caseData = new HashMap<String, Object>();
        caseData.put("recipientEmailAddress", "test@tester.com");
        CaseDetails request = CaseDetails.builder().data(caseData).id(CASE_ID).build();

        mockMvc.perform(post(path)
                            .content(new ObjectMapper().writeValueAsString(request))
                            .contentType(APPLICATION_JSON_VALUE)
                            .header("Authorization", "xxx")
                            .header("ServiceAuthorization", "xxx"))
            .andExpect(status().isOk());


        verify(hearingRecordingService, times(1))
            .findByCaseId(CASE_ID);
        verify(hearingRecordingShareesService, times(1))
            .createAndSaveEntry("test@tester.com", HEARING_RECORDING);
    }

}
