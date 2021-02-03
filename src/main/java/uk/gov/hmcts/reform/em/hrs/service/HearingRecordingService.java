package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.repository.FolderRepository;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;

import java.util.Optional;
import java.util.UUID;

//import java.util.*;
//import javax.transaction.Transactional;
//import javax.validation.constraints.NotNull;
//import java.io.IOException;
//import java.io.UncheckedIOException;
//import java.sql.SQLException;
//import java.util.stream.Collectors;
//import lombok.NonNull;
//import org.hibernate.HibernateException;

@Service
public class HearingRecordingService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private HearingRecordingRepository storedDocumentRepository;


//    @Autowired
//    private ToggleConfiguration toggleConfiguration;

//    @Autowired
//    private AzureStorageConfiguration azureStorageConfiguration;

    @Autowired
    private SecurityUtilService securityUtilService;

    //@Autowired
    //private BlobStorageWriteService blobStorageWriteService;
    //
    //@Autowired
    //private BlobStorageDeleteService blobStorageDeleteService;

    public Optional<HearingRecording> findOne(UUID id) {
        Optional<HearingRecording> storedDocument = storedDocumentRepository.findById(id);
        if (storedDocument.isPresent() && storedDocument.get().isDeleted()) {
            return Optional.empty();
        }
        return storedDocument;
    }

    public Optional<HearingRecording> findOneWithBinaryData(UUID id) {
        Optional<HearingRecording> storedDocument = storedDocumentRepository.findById(id);
        if (storedDocument.isPresent() && storedDocument.get().isHardDeleted()) {
            return Optional.empty();
        }
        return storedDocument;
    }

    public HearingRecording save(HearingRecording storedDocument) {
        return storedDocumentRepository.save(storedDocument);
    }


//Note this work crosses over heavily with    https://tools.hmcts.net/jira/browse/EM-3385 so will be completed as part of that
//
//    public void saveItemsToBucket(Folder folder, List<MultipartFile> files) {
//        String userId = securityUtilService.getUserId();
//        List<HearingRecording> items = files.stream().map(file -> {
//            HearingRecording storedDocument = new HearingRecording();
//            storedDocument.setFolder(folder);
//            storedDocument.setCreatedBy(userId);
//            storedDocument.setLastModifiedBy(userId);
//            final DocumentContentVersion documentContentVersion = new DocumentContentVersion(storedDocument,
//                                                                                             file,
//                                                                                             userId,
//                                                                                             azureStorageConfiguration
//                                                                                     .isPostgresBlobStorageEnabled());
//            storedDocument.getDocumentContentVersions().add(documentContentVersion);
//
//            save(storedDocument);
//            storeInAzureBlobStorage(storedDocument, documentContentVersion, file);
//            closeBlobInputStream(documentContentVersion);
//            return storedDocument;
//        }).collect(Collectors.toList());
//
//        folder.getHearingRecordings().addAll(items);
//
//        folderRepository.save(folder);
//    }
//
//    public List<HearingRecording> saveItems(UploadDocumentsCommand uploadDocumentsCommand) {
//        String userId = securityUtilService.getUserId();
//        return uploadDocumentsCommand.getFiles().stream().map(file -> {
//            HearingRecording document = new HearingRecording();
//            document.setCreatedBy(userId);
//            document.setLastModifiedBy(userId);
//            document.setClassification(uploadDocumentsCommand.getClassification());
//            document.setRoles(uploadDocumentsCommand.getRoles() != null
//                ? uploadDocumentsCommand.getRoles().stream().collect(Collectors.toSet()) : null);
//
//            if (toggleConfiguration.isMetadatasearchendpoint()) {
//                document.setMetadata(uploadDocumentsCommand.getMetadata());
//            }
//            if (toggleConfiguration.isTtl()) {
//                document.setTtl(uploadDocumentsCommand.getTtl());
//            }
//            DocumentContentVersion documentContentVersion = new DocumentContentVersion(document,
//                                                                                       file,
//                                                                                       userId,
//                                                                                       azureStorageConfiguration
//                                                                                   .isPostgresBlobStorageEnabled());
//            document.getDocumentContentVersions().add(documentContentVersion);
//            save(document);
//            storeInAzureBlobStorage(document, documentContentVersion, file);
//            closeBlobInputStream(documentContentVersion);
//            return document;
//        }).collect(Collectors.toList());
//
//    }
//
//    public List<HearingRecording> saveItems(List<MultipartFile> files) {
//        UploadDocumentsCommand command = new UploadDocumentsCommand();
//        command.setFiles(files);
//        return saveItems(command);
//    }
//
//    @Transactional
//    public void updateItems(UpdateDocumentsCommand command) {
//        for (DocumentUpdate update : command.documents) {
//            findOne(update.documentId).ifPresent(d -> updateHearingRecording(d, d.getTtl(), update.metadata));
//        }
//    }
//
//    public DocumentContentVersion addHearingRecordingVersion(HearingRecording storedDocument, MultipartFile file) {
//        DocumentContentVersion documentContentVersion = new DocumentContentVersion(storedDocument,
//                                                                                   file,
//                                                                                   securityUtilService.getUserId(),
//                                                                                   azureStorageConfiguration
//                                                                                 .isPostgresBlobStorageEnabled());
//        storedDocument.getDocumentContentVersions().add(documentContentVersion);
//        documentContentVersionRepository.save(documentContentVersion);
//        storeInAzureBlobStorage(storedDocument, documentContentVersion, file);
//        closeBlobInputStream(documentContentVersion);
//
//        return documentContentVersion;
//    }
//
//    public void deleteDocument(HearingRecording storedDocument, boolean permanent) {
//        storedDocument.setDeleted(true);
//        if (permanent) {
//            storedDocument.setHardDeleted(true);
//            storedDocument.getDocumentContentVersions().parallelStream().forEach(documentContentVersion -> {
//                if (azureStorageConfiguration.isAzureBlobStoreEnabled()) {
//                    blobStorageDeleteService.deleteDocumentContentVersion(documentContentVersion);
//                } else if (documentContentVersion.getDocumentContent() != null) {
//                    documentContentRepository.delete(documentContentVersion.getDocumentContent());
//                    documentContentVersion.setDocumentContent(null);
//                }
//            });
//        }
//        storedDocumentRepository.save(storedDocument);
//    }
//
//    public void updateHearingRecording(@NonNull HearingRecording storedDocument,
//    @NonNull UpdateDocumentCommand command) {
//        updateHearingRecording(storedDocument, command.getTtl(), null);
//    }
//
//    public void updateHearingRecording(
//        @NonNull HearingRecording storedDocument,
//        Date ttl,
//        Map<String, String> metadata
//    ) {
//        if (storedDocument.isDeleted()) {
//            return;
//        }
//
//        if (metadata != null) {
//            storedDocument.getMetadata().putAll(metadata);
//        }
//
//        storedDocument.setTtl(ttl);
//        storedDocument.setLastModifiedBy(securityUtilService.getUserId());
//        save(storedDocument);
//    }
//
//    public List<HearingRecording> findAllExpiredHearingRecordings() {
//        return storedDocumentRepository.findByTtlLessThanAndHardDeleted(new Date(), false);
//    }
//
//    private void storeInAzureBlobStorage(HearingRecording storedDocument,
//                                         DocumentContentVersion documentContentVersion,
//                                         MultipartFile file) {
//        if (azureStorageConfiguration.isAzureBlobStoreEnabled()) {
//            blobStorageWriteService.uploadDocumentContentVersion(storedDocument,
//                documentContentVersion,
//                file);
//        }
//    }
//
//    /**
//     * Force closure of the persisted <code>Blob</code>'s <code>InputStream</code>, to ensure the file handle is
//     * released.
//     *
//     * @param documentContentVersion <code>DocumentContentVersion</code> instance wrapping a
//     *                               <code>DocumentContent</code> that contains the <code>Blob</code>
//     * @throws HibernateException If the <code>Blob</code>'s stream cannot be accessed
//     * @throws UncheckedIOException If the stream cannot be closed
//     */
//    private void closeBlobInputStream(@NotNull final DocumentContentVersion documentContentVersion) {
//        try {
//            if (documentContentVersion.getDocumentContent() != null) {
//                documentContentVersion.getDocumentContent().getData().getBinaryStream().close();
//            }
//        } catch (SQLException e) {
//            throw new HibernateException("Unable to access blob stream", e);
//        } catch (IOException e) {
//            throw new UncheckedIOException("Unable to close blob stream", e);
//        }
//    }
}
