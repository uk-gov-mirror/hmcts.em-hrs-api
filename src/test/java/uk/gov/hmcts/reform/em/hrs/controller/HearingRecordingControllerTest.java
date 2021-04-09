package uk.gov.hmcts.reform.em.hrs.controller;

import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.hrs.componenttests.AbstractBaseTest;
import uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.FolderService;
import uk.gov.hmcts.reform.em.hrs.service.ShareService;
import uk.gov.hmcts.reform.em.hrs.util.IngestionQueue;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

import static java.util.Collections.emptySet;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.AUTHORIZATION_TOKEN;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.CASE_REFERENCE;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.CCD_CASE_ID;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.HEARING_RECORDING_DTO;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.RECORDING_DATETIME;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.RECORDING_REFERENCE;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.SERVICE_AUTHORIZATION_TOKEN;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.SHAREE_EMAIL_ADDRESS;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.convertObjectToJsonString;

class HearingRecordingControllerTest extends AbstractBaseTest {
    @MockBean
    private FolderService folderService;

    @MockBean
    private ShareService shareService;

    @Inject
    private IngestionQueue ingestionQueue;

    private static final String TEST_FOLDER = "folder-1";

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
                x -> x.node("folder-name").isPresent().isEqualTo(TEST_FOLDER),
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
    void testShouldGrantShareeDownloadAccessToHearingRecording() throws Exception {
        final String path = "/sharees";
        final CaseDetails request = CaseDetails.builder()
            .data(Map.of("recipientEmailAddress", SHAREE_EMAIL_ADDRESS))
            .id(CCD_CASE_ID)
            .build();

        doNothing().when(shareService).executeNotify(CCD_CASE_ID, SHAREE_EMAIL_ADDRESS, AUTHORIZATION_TOKEN);

        mockMvc.perform(post(path)
                            .content(convertObjectToJsonString(request))
                            .contentType(APPLICATION_JSON_VALUE)
                            .header("Authorization", AUTHORIZATION_TOKEN)
                            .header("ServiceAuthorization", SERVICE_AUTHORIZATION_TOKEN))
            .andExpect(status().isOk());

        verify(shareService, times(1)).executeNotify(CCD_CASE_ID, SHAREE_EMAIL_ADDRESS, AUTHORIZATION_TOKEN);
    }

    @Test
    void testShouldNotExceedOneSecond() throws Exception {
        final String path = "/segments";

        final Instant start = Instant.now(Clock.systemDefaultZone());

        mockMvc.perform(post(path)
                            .content(convertObjectToJsonString(HEARING_RECORDING_DTO))
                            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted())
            .andReturn();

        final Instant end = Instant.now(Clock.systemDefaultZone());

        assertThat(Duration.between(start, end)).isLessThanOrEqualTo(Duration.ofSeconds(1L));
    }

}
