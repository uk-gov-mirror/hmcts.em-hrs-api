package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.domain.JobInProgress;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;
import uk.gov.hmcts.reform.em.hrs.repository.JobInProgressRepository;
import uk.gov.hmcts.reform.em.hrs.storage.HearingRecordingStorage;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class HearingRecordingServiceImplTests {
    @Mock
    private JobInProgressRepository jobInProgressRepository;
    @Mock
    private HearingRecordingRepository hearingRecordingRepository;
    @Mock
    private HearingRecordingStorage hearingRecordingStorage;
    @InjectMocks
    private HearingRecordingServiceImpl underTest;

    private static final String TEST_FOLDER = "folder-1";
    private static final String FILE_1 = "f1.mp4";
    private static final String FILE_2 = "f2.mp4";
    private static final String FILE_3 = "f3.mp4";

    @Test
    void testShouldReturnEmptySetWhenNoCompletedFilesAndNoInProgressFilesFound() {
        doReturn(Collections.emptyList()).when(jobInProgressRepository).findByFolder(TEST_FOLDER);
        doReturn(Collections.emptyList()).when(hearingRecordingRepository).findByFolder(TEST_FOLDER);
        doReturn(Collections.emptySet()).when(hearingRecordingStorage).findByFolder(TEST_FOLDER);
        final Set<String> expectedFilenames = Collections.emptySet();

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER);

        assertThat(actualFilenames).hasSameElementsAs(expectedFilenames);
    }

    @Test
    void testShouldReturnCompletedFilesWhenCompletedFilesButNoInProgressFilesFound() {
        doReturn(Collections.emptyList()).when(jobInProgressRepository).findByFolder(TEST_FOLDER);
        doReturn(createMetadataFilenames(Set.of(FILE_1, FILE_2, FILE_3)))
            .when(hearingRecordingRepository).findByFolder(TEST_FOLDER);
        doReturn(Set.of(FILE_1, FILE_2, FILE_3)).when(hearingRecordingStorage).findByFolder(TEST_FOLDER);
        final Set<String> expectedFilenames = Set.of(FILE_1, FILE_2, FILE_3);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER);

        assertThat(actualFilenames).hasSameElementsAs(expectedFilenames);
    }

    @Test
    void testShouldReturnInProgressFilesWhenNoCompletedFilesButInProgressFilesFound() {
        doReturn(createJobsInProgress(Set.of(FILE_1, FILE_2))).when(jobInProgressRepository).findByFolder(TEST_FOLDER);
        doReturn(Collections.emptyList()).when(hearingRecordingRepository).findByFolder(TEST_FOLDER);
        doReturn(Collections.emptySet()).when(hearingRecordingStorage).findByFolder(TEST_FOLDER);
        final Set<String> expectedFilenames = Set.of(FILE_1, FILE_2);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER);

        assertThat(actualFilenames).hasSameElementsAs(expectedFilenames);
    }

    @Test
    void testShouldReturnInProgressFilesAndCompletedFilesWhenCompletedFilesAndInProgressFilesFound() {
        doReturn(createJobsInProgress(Set.of(FILE_3))).when(jobInProgressRepository).findByFolder(TEST_FOLDER);
        doReturn(createMetadataFilenames(Set.of(FILE_1, FILE_2)))
            .when(hearingRecordingRepository).findByFolder(TEST_FOLDER);
        doReturn(Set.of(FILE_1, FILE_2)).when(hearingRecordingStorage).findByFolder(TEST_FOLDER);
        final Set<String> expectedFilenames = Set.of(FILE_1, FILE_2, FILE_3);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER);

        assertThat(actualFilenames).hasSameElementsAs(expectedFilenames);
    }

    @Test
    void testShouldReturnUnionOfInProgressFilesAndCompletedFilesWhenFileAppearsInBothInProgressAndCompleted() {
        doReturn(createJobsInProgress(Set.of(FILE_2, FILE_3))).when(jobInProgressRepository).findByFolder(TEST_FOLDER);
        doReturn(createMetadataFilenames(Set.of(FILE_1, FILE_2)))
            .when(hearingRecordingRepository).findByFolder(TEST_FOLDER);
        doReturn(Set.of(FILE_1, FILE_2)).when(hearingRecordingStorage).findByFolder(TEST_FOLDER);
        final Set<String> expectedFilenames = Set.of(FILE_1, FILE_2, FILE_3);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER);

        assertThat(actualFilenames).hasSameElementsAs(expectedFilenames);
    }

    @Test
    void testShouldExcludeFileWhenFileIsPresentInMetadataDatabaseButMissingInBlobstore() {
        doReturn(Collections.emptyList()).when(jobInProgressRepository).findByFolder(TEST_FOLDER);
        doReturn(createMetadataFilenames(Set.of(FILE_1, FILE_2, FILE_3)))
            .when(hearingRecordingRepository).findByFolder(TEST_FOLDER);
        doReturn(Set.of(FILE_2, FILE_3)).when(hearingRecordingStorage).findByFolder(TEST_FOLDER);
        final Set<String> expectedFilenames = Set.of(FILE_2, FILE_3);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER);

        assertThat(actualFilenames).hasSameElementsAs(expectedFilenames);
    }

    @Test
    void testShouldExcludeFileWhenFileIsPresentInBlobstoreButMissingInMetadataDatabase() {
        doReturn(Collections.emptyList()).when(jobInProgressRepository).findByFolder(TEST_FOLDER);
        doReturn(createMetadataFilenames(Set.of(FILE_2, FILE_3)))
            .when(hearingRecordingRepository).findByFolder(TEST_FOLDER);
        doReturn(Set.of(FILE_1, FILE_2, FILE_3)).when(hearingRecordingStorage).findByFolder(TEST_FOLDER);
        final Set<String> expectedFilenames = Set.of(FILE_2, FILE_3);

        final Set<String> actualFilenames = underTest.getStoredFiles(TEST_FOLDER);

        assertThat(actualFilenames).hasSameElementsAs(expectedFilenames);
    }

    private List<HearingRecording> createMetadataFilenames(final Set<String> filenames) {
        return filenames.stream()
            .map(x -> HearingRecording.builder().segments(buildSingleHearingRecordingSegments(x)).build())
            .collect(Collectors.toUnmodifiableList());
    }

    private Set<HearingRecordingSegment> buildSingleHearingRecordingSegments(final String filename) {
        return singleton(HearingRecordingSegment.builder().fileName(filename).build());
    }

    private List<JobInProgress> createJobsInProgress(final Set<String> filenames) {
        return filenames.stream()
            .map(x -> JobInProgress.builder().filename(x).build())
            .collect(Collectors.toUnmodifiableList());
    }






    //    @Mock
    //    private HearingRecordingRepository hearingRecordingRepository;
    //
    ////
    ////    @Mock
    ////    private ToggleConfiguration toggleConfiguration;
    //
    //    @Mock
    //    private FolderRepository folderRepository;
    //
    ////    @Mock
    ////    private BlobStorageWriteService blobStorageWriteService;
    //
    //    @Mock
    //    private SecurityUtilService securityUtilService;
    //
    ////    @Mock
    ////    private AzureStorageConfiguration azureStorageConfiguration;
    ////
    ////    @Mock
    ////    private BlobStorageDeleteService blobStorageDeleteService;

    //    @Before
    //    public void setUp() {
    //        when(securityUtilService.getUserId()).thenReturn("Corín Tellado");
    //    }


    //    @Test
    //    public void testFindOne() {
    //        when(this.hearingRecordingRepository.findById(any(UUID.class))).thenReturn(Optional.of(TestUtil
    //        .STORED_DOCUMENT));
    //        Optional<HearingRecording> hearingRecording = hearingRecordingService.findOne(TestUtil.RANDOM_UUID);
    //        assertThat(hearingRecording.get(), equalTo(TestUtil.STORED_DOCUMENT));
    //    }
    //
    //    @Test
    //    public void testFindOneThatDoesNotExist() {
    //        when(this.hearingRecordingRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
    //        Optional<HearingRecording> hearingRecording = hearingRecordingService.findOne(TestUtil.RANDOM_UUID);
    //        assertFalse(hearingRecording.isPresent());
    //    }
    //
    //    @Test
    //    public void testFindOneThatIsMarkedDeleted() {
    //        when(this.hearingRecordingRepository.findById(any(UUID.class))).thenReturn(Optional.of(DELETED_DOCUMENT));
    //        Optional<HearingRecording> hearingRecording = hearingRecordingService.findOne(TestUtil.RANDOM_UUID);
    //        assertFalse(hearingRecording.isPresent());
    //    }
    //
    //    @Test
    //    public void testFindOneWithBinaryDataThatDoesNotExist() {
    //        when(this.hearingRecordingRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
    //        Optional<HearingRecording> hearingRecording = hearingRecordingService.findOneWithBinaryData(TestUtil
    //        .RANDOM_UUID);
    //        assertFalse(hearingRecording.isPresent());
    //    }
    //
    //    @Test
    //    public void testFindOneWithBinaryDataThatIsMarkedHardDeleted() {
    //        when(this.hearingRecordingRepository.findById(any(UUID.class))).thenReturn(Optional.of
    //        (HARD_DELETED_DOCUMENT));
    //        Optional<HearingRecording> hearingRecording = hearingRecordingService.findOneWithBinaryData(TestUtil
    //        .RANDOM_UUID);
    //        assertFalse(hearingRecording.isPresent());
    //    }
    //
    //    @Test
    //    public void testFindOneWithBinaryDataThatIsMarkedDeleted() {
    //        when(this.hearingRecordingRepository.findById(any(UUID.class))).thenReturn(Optional.of(DELETED_DOCUMENT));
    //        Optional<HearingRecording> hearingRecording = hearingRecordingService.findOneWithBinaryData(TestUtil
    //        .RANDOM_UUID);
    //        assertTrue(hearingRecording.isPresent());
    //    }
    //
    //    @Test
    //    public void testSave() {
    //        final HearingRecording hearingRecording = TestUtil.STORED_DOCUMENT;
    //        hearingRecordingService.save(hearingRecording);
    //        verify(hearingRecordingRepository).save(hearingRecording);
    //    }
    //
    //    @Test
    //    public void testSaveItemsWithCommand() {
    //        UploadDocumentsCommand uploadDocumentsCommand = new UploadDocumentsCommand();
    //        uploadDocumentsCommand.setFiles(singletonList(TEST_FILE));
    //        uploadDocumentsCommand.setRoles(ImmutableList.of("a", "b"));
    //        uploadDocumentsCommand.setClassification(PRIVATE);
    //        uploadDocumentsCommand.setMetadata(ImmutableMap.of("prop1", "value1"));
    //        uploadDocumentsCommand.setTtl(new Date());
    //
    //        when(hearingRecordingRepository.save(any(HearingRecording.class))).thenReturn(new HearingRecording());
    //        List<HearingRecording> documents = hearingRecordingService.saveItems(uploadDocumentsCommand);
    //
    //        final HearingRecording hearingRecording = documents.get(0);
    //        final DocumentContentVersion latestVersion = hearingRecording.getDocumentContentVersions().get(0);
    //
    //        assertEquals(1, documents.size());
    //        assertEquals(hearingRecording.getRoles(), newHashSet("a", "b"));
    //        assertEquals(hearingRecording.getClassification(), PRIVATE);
    //        Assert.assertNull(hearingRecording.getMetadata());
    //        Assert.assertNull(hearingRecording.getTtl());
    //        assertEquals(TEST_FILE.getContentType(), latestVersion.getMimeType());
    //        assertEquals(TEST_FILE.getOriginalFilename(), latestVersion.getOriginalDocumentName());
    //    }
    //
    //    @Test
    //    public void testSaveItemsWithCommandAndToggleConfiguration() {
    //
    //        when(toggleConfiguration.isMetadatasearchendpoint()).thenReturn(true);
    //        when(toggleConfiguration.isTtl()).thenReturn(true);
    //
    //        UploadDocumentsCommand uploadDocumentsCommand = new UploadDocumentsCommand();
    //        uploadDocumentsCommand.setFiles(singletonList(TEST_FILE));
    //        uploadDocumentsCommand.setRoles(ImmutableList.of("a", "b"));
    //        uploadDocumentsCommand.setClassification(PRIVATE);
    //        uploadDocumentsCommand.setMetadata(ImmutableMap.of("prop1", "value1"));
    //        uploadDocumentsCommand.setTtl(new Date());
    //
    //        List<HearingRecording> documents = hearingRecordingService.saveItems(uploadDocumentsCommand);
    //
    //        final HearingRecording hearingRecording = documents.get(0);
    //        final DocumentContentVersion latestVersion = hearingRecording.getDocumentContentVersions().get(0);
    //
    //        assertEquals(1, documents.size());
    //        assertEquals(hearingRecording.getRoles(), newHashSet("a", "b"));
    //        assertEquals(hearingRecording.getClassification(), PRIVATE);
    //        assertEquals(hearingRecording.getMetadata(), ImmutableMap.of("prop1", "value1"));
    //        Assert.assertNotNull(hearingRecording.getTtl());
    //        assertEquals(TEST_FILE.getContentType(), latestVersion.getMimeType());
    //        assertEquals(TEST_FILE.getOriginalFilename(), latestVersion.getOriginalDocumentName());
    //    }
    //
    //    @Test
    //    public void testSaveItems() {
    //        List<HearingRecording> documents = hearingRecordingService.saveItems(singletonList(TEST_FILE));
    //
    //        final DocumentContentVersion latestVersion = documents.get(0).getDocumentContentVersions().get(0);
    //
    //        assertEquals(1, documents.size());
    //        assertEquals(TEST_FILE.getContentType(), latestVersion.getMimeType());
    //        assertEquals(TEST_FILE.getOriginalFilename(), latestVersion.getOriginalDocumentName());
    //        verifyNoMoreInteractions(blobStorageWriteService);
    //    }
    //
    //    @Test
    //    public void testSaveItemsToAzure() {
    //        setupStorageOptions(true, false);
    //        List<HearingRecording> documents = hearingRecordingService.saveItems(singletonList(TEST_FILE));
    //
    //        assertEquals(1, documents.size());
    //
    //        final DocumentContentVersion latestVersion = documents.get(0).getDocumentContentVersions().get(0);
    //
    //        assertEquals(TEST_FILE.getContentType(), latestVersion.getMimeType());
    //        assertEquals(TEST_FILE.getOriginalFilename(), latestVersion.getOriginalDocumentName());
    //        verify(blobStorageWriteService).uploadDocumentContentVersion(documents.get(0), latestVersion, TEST_FILE);
    //    }
    //
    //    @Test
    //    public void testAddHearingRecordingVersion() {
    //
    //        setupStorageOptions(false, true);
    //        HearingRecording hearingRecording = new HearingRecording();
    //
    //        DocumentContentVersion documentContentVersion = hearingRecordingService.addHearingRecordingVersion(
    //            hearingRecording, TEST_FILE);
    //
    //        assertThat(hearingRecording.getDocumentContentVersions().size(), equalTo(1));
    //
    //        assertThat(documentContentVersion, notNullValue());
    //
    //        final DocumentContentVersion latestVersion = hearingRecording.getDocumentContentVersions().get(0);
    //        assertThat(latestVersion.getMimeType(), equalTo(TEST_FILE.getContentType()));
    //        assertThat(latestVersion.getOriginalDocumentName(), equalTo(TEST_FILE.getOriginalFilename()));
    //
    //        ArgumentCaptor<DocumentContentVersion> captor = ArgumentCaptor.forClass(DocumentContentVersion.class);
    //        verify(documentContentVersionRepository).save(captor.capture());
    //        assertThat(captor.getValue(), is(documentContentVersion));
    //    }
    //
    //    @Test
    //    public void testAddHearingRecordingVersionWhenAzureBlobStoreEnabled() {
    //
    //        setupStorageOptions(true, false);
    //        HearingRecording hearingRecording = new HearingRecording();
    //
    //        DocumentContentVersion documentContentVersion = hearingRecordingService.addHearingRecordingVersion(
    //            hearingRecording, TEST_FILE);
    //
    //        assertThat(hearingRecording.getDocumentContentVersions().size(), equalTo(1));
    //        assertThat(documentContentVersion, notNullValue());
    //
    //        final DocumentContentVersion latestVersion = hearingRecording.getDocumentContentVersions().get(0);
    //        assertThat(latestVersion.getMimeType(), equalTo(TEST_FILE.getContentType()));
    //        assertThat(latestVersion.getOriginalDocumentName(), equalTo(TEST_FILE.getOriginalFilename()));
    //
    //        ArgumentCaptor<DocumentContentVersion> captor = ArgumentCaptor.forClass(DocumentContentVersion.class);
    //        verify(blobStorageWriteService).uploadDocumentContentVersion(hearingRecording, documentContentVersion,
    //        TEST_FILE);
    //        verify(documentContentVersionRepository).save(captor.capture());
    //        assertThat(captor.getValue(), is(documentContentVersion));
    //    }
    //
    //    @Test
    //    public void testDelete() {
    //        HearingRecording hearingRecording = new HearingRecording();
    //        hearingRecordingService.deleteDocument(hearingRecording, false);
    //
    //        assertThat(hearingRecording.isDeleted(), is(true));
    //        verify(hearingRecordingRepository).save(hearingRecording);
    //    }
    //
    //    @Test
    //    public void testHardDelete() {
    //        DocumentContent documentContent = new DocumentContent(mock(Blob.class));
    //        HearingRecording hearingRecordingWithContent = HearingRecording.builder()
    //            .documentContentVersions(ImmutableList.of(DocumentContentVersion.builder()
    //                .documentContent(documentContent)
    //                .build()))
    //            .build();
    //
    //        hearingRecordingService.deleteDocument(hearingRecordingWithContent, true);
    //
    //        assertThat(hearingRecordingWithContent.getMostRecentDocumentContentVersion().getDocumentContent(),
    //        nullValue());
    //        verify(hearingRecordingRepository, atLeastOnce()).save(hearingRecordingWithContent);
    //        verify(documentContentRepository).delete(documentContent);
    //    }
    //
    //    @Test
    //    public void testHardDeleteAzureBlobEnabled() {
    //        HearingRecording hearingRecordingWithContent = HearingRecording.builder()
    //            .documentContentVersions(ImmutableList.of(DocumentContentVersion.builder()
    //                .build()))
    //            .build();
    //
    //        when(azureStorageConfiguration.isAzureBlobStoreEnabled()).thenReturn(true);
    //
    //        hearingRecordingService.deleteDocument(hearingRecordingWithContent, true);
    //
    //        verify(hearingRecordingRepository, atLeastOnce()).save(hearingRecordingWithContent);
    //        verify(blobStorageDeleteService)
    //            .deleteDocumentContentVersion(hearingRecordingWithContent.getMostRecentDocumentContentVersion());
    //    }
    //
    //    @Test
    //    public void testHardDeleteWithManyVersions() {
    //        DocumentContentVersion contentVersion = DocumentContentVersion.builder()
    //            .documentContent(new DocumentContent(mock(Blob.class)))
    //            .build();
    //
    //        DocumentContentVersion secondContentVersion = DocumentContentVersion.builder()
    //            .documentContent(new DocumentContent(mock(Blob.class)))
    //            .build();
    //
    //        HearingRecording hearingRecordingWithContent = HearingRecording.builder()
    //            .documentContentVersions(Arrays.asList(contentVersion, secondContentVersion))
    //            .build();
    //
    //        hearingRecordingService.deleteDocument(hearingRecordingWithContent, true);
    //
    //        hearingRecordingWithContent.getDocumentContentVersions().forEach(documentContentVersion -> {
    //            assertThat(documentContentVersion.getDocumentContent(), nullValue());
    //        });
    //        verify(hearingRecordingRepository, atLeastOnce()).save(hearingRecordingWithContent);
    //        verify(documentContentRepository, times(2)).delete(Mockito.any(DocumentContent.class));
    //    }
    //
    //    @Test
    //    public void testSaveItemsToBucket() {
    //        Folder folder = new Folder();
    //
    //        hearingRecordingService.saveItemsToBucket(folder, Stream.of(TEST_FILE).collect(Collectors.toList()));
    //
    //        assertThat(folder.getHearingRecordings().size(), equalTo(1));
    //
    //        final DocumentContentVersion latestVersionInFolder = folder.getHearingRecordings().get(0)
    //        .getDocumentContentVersions().get(0);
    //
    //        assertThat(latestVersionInFolder.getMimeType(), equalTo(TEST_FILE.getContentType()));
    //        assertThat(latestVersionInFolder.getOriginalDocumentName(), equalTo(TEST_FILE.getOriginalFilename()));
    //        verify(securityUtilService).getUserId();
    //        verify(folderRepository).save(folder);
    //        verifyNoMoreInteractions(blobStorageWriteService);
    //    }
    //
    //    @Test
    //    public void testSaveItemsToBucketToBlobStore() throws Exception {
    //        Folder folder = new Folder();
    //        setupStorageOptions(true, false);
    //        hearingRecordingService.saveItemsToBucket(folder, Stream.of(TEST_FILE).collect(Collectors.toList()));
    //
    //        assertThat(folder.getHearingRecordings().size(), equalTo(1));
    //
    //        final DocumentContentVersion latestVersionInFolder = folder.getHearingRecordings().get(0)
    //        .getDocumentContentVersions().get(0);
    //
    //        assertThat(latestVersionInFolder.getMimeType(), equalTo(TEST_FILE.getContentType()));
    //        assertThat(latestVersionInFolder.getOriginalDocumentName(), equalTo(TEST_FILE.getOriginalFilename()));
    //        verify(securityUtilService).getUserId();
    //        verify(folderRepository).save(folder);
    //        verify(blobStorageWriteService).uploadDocumentContentVersion(folder.getHearingRecordings().get(0),
    //        latestVersionInFolder, TEST_FILE);
    //    }
    //
    //    @Test
    //    public void testUpdateItems() {
    //        HearingRecording hearingRecording = new HearingRecording();
    //        hearingRecording.setId(UUID.randomUUID());
    //        hearingRecording.setMetadata(Maps.newHashMap("Key", "Value"));
    //
    //        when(hearingRecordingRepository.findById(any(UUID.class))).thenReturn(Optional.of(hearingRecording));
    //
    //        DocumentUpdate update = new DocumentUpdate(hearingRecording.getId(), Maps.newHashMap("UpdateKey",
    //        "UpdateValue"));
    //        UpdateDocumentsCommand command = new UpdateDocumentsCommand(null, singletonList(update));
    //
    //        hearingRecordingService.updateItems(command);
    //
    //        assertEquals(hearingRecording.getMetadata().get("Key"), "Value");
    //        assertEquals(hearingRecording.getMetadata().get("UpdateKey"), "UpdateValue");
    //    }
    //
    //    @Test
    //    public void testUpdateDocument() {
    //        HearingRecording hearingRecording = new HearingRecording();
    //        UpdateDocumentCommand command = new UpdateDocumentCommand();
    //        Date newTtl = new Date();
    //        command.setTtl(newTtl);
    //        hearingRecordingService.updateHearingRecording(hearingRecording, command);
    //        assertEquals(newTtl, hearingRecording.getTtl());
    //    }
    //
    //    @Test
    //    public void testUpdateDocumentWithMetaData() {
    //        HearingRecording hearingRecording = new HearingRecording();
    //        hearingRecording.setMetadata(Maps.newHashMap("Key", "Value"));
    //
    //        Date newTtl = new Date();
    //        hearingRecordingService.updateHearingRecording(hearingRecording, newTtl, Maps.newHashMap("UpdateKey",
    //        "UpdateValue"));
    //
    //        assertEquals(newTtl, hearingRecording.getTtl());
    //        assertEquals(hearingRecording.getMetadata().get("Key"), "Value");
    //        assertEquals(hearingRecording.getMetadata().get("UpdateKey"), "UpdateValue");
    //    }
    //
    //    @Test
    //    public void testUpdateDeletedDocument() {
    //        HearingRecording hearingRecording = new HearingRecording();
    //        hearingRecording.setDeleted(true);
    //        UpdateDocumentCommand command = new UpdateDocumentCommand();
    //        Date newTtl = new Date();
    //        command.setTtl(newTtl);
    //        hearingRecordingService.updateHearingRecording(hearingRecording, command);
    //        Assert.assertNull(hearingRecording.getTtl());
    //    }
    //
    //    @Test
    //    public void testFindAllExpiredHearingRecordings() {
    //        hearingRecordingService.findAllExpiredHearingRecordings();
    //        verify(hearingRecordingRepository, times(1)).findByTtlLessThanAndHardDeleted(any(), any());
    //    }
    //
    //    @Test(expected = NullPointerException.class)
    //    public void testUpdateHearingRecordingNullHearingRecording() {
    //        hearingRecordingService.updateHearingRecording(null, new UpdateDocumentCommand());
    //    }
    //
    //    @Test(expected = NullPointerException.class)
    //    public void testUpdateHearingRecordingNullCommand() {
    //        hearingRecordingService.updateHearingRecording(new HearingRecording(), null);
    //    }
    //
    //    private void setupStorageOptions(Boolean azureEnabled, Boolean postgresEnabled) {
    //        when(azureStorageConfiguration.isAzureBlobStoreEnabled()).thenReturn(azureEnabled);
    //        when(azureStorageConfiguration.isPostgresBlobStorageEnabled()).thenReturn(postgresEnabled);
    //    }
}

