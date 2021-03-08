package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil;
import uk.gov.hmcts.reform.em.hrs.domain.Folder;
import uk.gov.hmcts.reform.em.hrs.repository.FolderRepository;
import uk.gov.hmcts.reform.em.hrs.storage.HearingRecordingStorage;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.FILE_1;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.FILE_2;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.FILE_3;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.FOLDER_WITH_JOBS_IN_PROGRESS;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.FOLDER_WITH_SEGMENT;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.FOLDER_WITH_SEGMENT_AND_IN_PROGRESS;

@ExtendWith(MockitoExtension.class)
class FolderServiceImplTests {
    @Mock
    private FolderRepository folderRepository;
    @Mock
    private HearingRecordingStorage hearingRecordingStorage;

    @InjectMocks
    private FolderServiceImpl underTest;

    private static final String TEST_FOLDER_NAME = "folder-1";

    @Test
    void testShouldFindOne() {
        doReturn(Optional.of(TestUtil.FOLDER)).when(this.folderRepository).findById(TestUtil.RANDOM_UUID);

        final Optional<Folder> folder = underTest.findById(TestUtil.RANDOM_UUID);

        assertThat(folder).isEqualTo(Optional.of(TestUtil.FOLDER));
    }

    @Test
    void testShouldSave() {
        final Folder folder = TestUtil.EMPTY_FOLDER;

        underTest.save(folder);

        verify(folderRepository, times(1)).save(folder);
    }

    @Test
    @DisplayName("Test when folder is not found in the database and blobstore")
    void testShouldReturnEmptyWhenFolderIsNotFound() {
        doReturn(Optional.empty()).when(folderRepository).findByName(TEST_FOLDER_NAME);
        doReturn(Collections.emptySet()).when(hearingRecordingStorage).findByFolder(TEST_FOLDER_NAME);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER_NAME);

        assertThat(actualFilenames).hasSameElementsAs(Collections.emptySet());
    }

    @Test
    @DisplayName("Test when folder has no HearingRecording data in the database, "
        + "no files in progress and no files in the blobstore")
    void testShouldReturnEmptyWhenFolderHasNoHearingRecordings() {
        doReturn(Optional.of(TestUtil.EMPTY_FOLDER)).when(folderRepository).findByName(TEST_FOLDER_NAME);
        doReturn(Collections.emptySet()).when(hearingRecordingStorage).findByFolder(TEST_FOLDER_NAME);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER_NAME);

        assertThat(actualFilenames).hasSameElementsAs(Collections.emptySet());
    }

    @Test
    @DisplayName("Test when folder has HearingRecording data with no segment in the database, "
        + "no files in progress and no files in the blobstore")
    void testShouldReturnEmptyWhenFolderHasHearingRecordingsWithNoSegments() {
        doReturn(Optional.of(TestUtil.FOLDER)).when(folderRepository).findByName(TEST_FOLDER_NAME);
        doReturn(Collections.emptySet()).when(hearingRecordingStorage).findByFolder(TEST_FOLDER_NAME);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER_NAME);

        assertThat(actualFilenames).hasSameElementsAs(Collections.emptySet());
    }

    @Test
    @DisplayName("Test when files are recorded in the database and the blobstore and no files in progress")
    void testShouldReturnCompletedFilesOnly() {
        doReturn(Optional.of(FOLDER_WITH_SEGMENT)).when(folderRepository).findByName(TEST_FOLDER_NAME);
        doReturn(Set.of(FILE_1, FILE_2, FILE_3)).when(hearingRecordingStorage).findByFolder(TEST_FOLDER_NAME);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER_NAME);

        assertThat(actualFilenames).hasSameElementsAs(Set.of(FILE_1, FILE_2, FILE_3));
    }

    @Test
    @DisplayName("Test when files are recorded in the database and blobstore and more files in progress")
    void testShouldReturnBothCompletedAndInProgressFiles() {
        doReturn(Optional.of(FOLDER_WITH_SEGMENT_AND_IN_PROGRESS)).when(folderRepository).findByName(TEST_FOLDER_NAME);
        doReturn(Set.of(FILE_1, FILE_2)).when(hearingRecordingStorage).findByFolder(TEST_FOLDER_NAME);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER_NAME);

        assertThat(actualFilenames).hasSameElementsAs(Set.of(FILE_1, FILE_2, FILE_3));
    }

    @Test
    @DisplayName("Test when files are recorded in the database but not in the blobstore and no files in progress")
    void testShouldExcludeWhenFileIsInDatabaseButNotInBlobstore() {
        doReturn(Optional.of(FOLDER_WITH_SEGMENT_AND_IN_PROGRESS)).when(folderRepository).findByName(TEST_FOLDER_NAME);
        doReturn(Collections.emptySet()).when(hearingRecordingStorage).findByFolder(TEST_FOLDER_NAME);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER_NAME);

        assertThat(actualFilenames).hasSameElementsAs(Set.of(FILE_3));
    }

    @Test
    @DisplayName("Test when files are recoded in the blobstore but not in the database and more files in progress")
    void testShouldReturnInProgressFilesOnly() {
        doReturn(Optional.of(FOLDER_WITH_JOBS_IN_PROGRESS)).when(folderRepository).findByName(TEST_FOLDER_NAME);
        doReturn(Set.of(FILE_3)).when(hearingRecordingStorage).findByFolder(TEST_FOLDER_NAME);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER_NAME);

        assertThat(actualFilenames).hasSameElementsAs(Set.of(FILE_1, FILE_2));
    }
}
