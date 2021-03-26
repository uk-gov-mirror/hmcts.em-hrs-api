package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class HearingRecordingServiceImplTests {
    @Mock
    private HearingRecordingRepository hearingRecordingRepository;

    @InjectMocks
    private HearingRecordingServiceImpl hearingRecordingServiceImpl;

    private HearingRecordingService hearingRecordingService;
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
    @Mock
    private SecurityUtilService securityUtilService;
    //
    ////    @Mock
    ////    private AzureStorageConfig azureStorageConfiguration;
    ////
    ////    @Mock
    ////    private BlobStorageDeleteService blobStorageDeleteService;

    @Before
    public void setUp() {
        Mockito.when(securityUtilService.getUserId()).thenReturn("Corín Tellado");
    }


    @Test
    public void testFindOne() {
        Mockito.when(this.hearingRecordingRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(TestUtil.HEARING_RECORDING));
        Optional<HearingRecording> hearingRecording = hearingRecordingServiceImpl.findOne(TestUtil.RANDOM_UUID);
        assert(hearingRecording.get()).equals(TestUtil.HEARING_RECORDING);
    }
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

