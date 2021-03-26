package uk.gov.hmcts.reform.em.hrs.controller;

import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.em.hrs.componenttests.AbstractBaseTest;
import uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil;
import uk.gov.hmcts.reform.em.hrs.service.FolderService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingSegmentService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingShareeService;
import uk.gov.hmcts.reform.em.hrs.service.ShareService;

import java.util.Set;
import java.util.UUID;

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

class HearingRecordingControllerTest extends AbstractBaseTest {

    @MockBean
    private FolderService folderService;

    @MockBean
    private HearingRecordingService hearingRecordingService;

    @MockBean
    private HearingRecordingSegmentService hearingRecordingSegmentService;

    @MockBean
    private HearingRecordingShareeService hearingRecordingShareeService;

    @MockBean
    private ShareService shareService;

    private static final String TEST_FOLDER = "folder-1";
    private static final UUID ID = TestUtil.HEARING_RECORDING.getId();

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
                x -> x.node("folder-name").isEqualTo(TEST_FOLDER),
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
//
//    @Test
//    public void testShareHearingRecording() throws Exception {
//        final String path = "/folders/" + TEST_FOLDER + "/hearing-recording/" + ID + "/access-right";
//
//        doReturn(Optional.of(TestUtil.HEARING_RECORDING)).when(hearingRecordingService).findOne(ID);
//
//        // make sure share service doesn't throw an error hear (if email not found in request)
//        // when shareservice hit make sure not thrown error
//
//        mockMvc.perform(post(path)
//                            .contentType(APPLICATION_JSON_VALUE)
//                            .header("Authorization", "xxx")
//                            .header("ServiceAuthorization", "xxx"))
//            .andExpect(status().isOk());
//
//        verify(shareServiceImpl, times(1))
//            .executeNotify(TestUtil.HEARING_RECORDING, any(HttpServletRequest.class));
//
//    }
//
//    @Test
//    public void testShareHearingRecordingNotFound() throws Exception {
//        final String path = "/folders/" + TEST_FOLDER + "/hearing-recording/" + ID + "/access-right";
//
//        mockMvc.perform(post(path)
//                            .contentType(APPLICATION_JSON_VALUE)
//                            .header("Authorization", "xxx")
//                            .header("ServiceAuthorization", "xxx"))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void testShareHearingRecordingBadRequest() throws Exception {
//        final String path = "/folders/" + TEST_FOLDER + "/hearing-recording/" + ID + "/access-right";
//
//        doReturn(Optional.of(TestUtil.HEARING_RECORDING)).when(hearingRecordingService).findOne(ID);
//        // this might be thrown naturally anyway
//        Mockito.doThrow(IllegalArgumentException.class)
//            .when(shareServiceImpl).executeNotify(Mockito.any(HearingRecording.class),
//                                                  Mockito.any(HttpServletRequest.class));
//
//        mockMvc.perform(post(path)
//                            .contentType(APPLICATION_JSON_VALUE)
//                            .header("Authorization", "xxx")
//                            .header("ServiceAuthorization", "xxx"))
//            .andExpect(status().isBadRequest());
//
//    }

}
