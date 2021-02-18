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
    private HearingRecordingRepository hearingRecordingRepository;


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
        Optional<HearingRecording> hearingRecording = hearingRecordingRepository.findById(id);
        if (hearingRecording.isPresent() && hearingRecording.get().isDeleted()) {
            return Optional.empty();
        }
        return hearingRecording;
    }

    public Optional<HearingRecording> findOneWithBinaryData(UUID id) {
        Optional<HearingRecording> hearingRecording = hearingRecordingRepository.findById(id);
        if (hearingRecording.isPresent() && hearingRecording.get().isHardDeleted()) {
            return Optional.empty();
        }
        return hearingRecording;
    }

    public HearingRecording save(HearingRecording hearingRecording) {
        return hearingRecordingRepository.save(hearingRecording);
    }


//Note this work crosses over heavily with    https://tools.hmcts.net/jira/browse/EM-3385 so will be completed as part of that
//
//    public void saveItemsToBucket(Folder folder, List<MultipartFile> files) {
//        String userId = securityUtilService.getUserId();
//        List<HearingRecording> items = files.stream().map(file -> {
//            HearingRecording hearingRecording = new HearingRecording();
//            hearingRecording.setFolder(folder);
//            hearingRecording.setCreatedBy(userId);
//            hearingRecording.setLastModifiedBy(userId);
//            final DocumentContentVersion documentContentVersion = new DocumentContentVersion(hearingRecording,
//                                                                                             file,
//                                                                                             userId,
//                                                                                             azureStorageConfiguration
//                                                                                     .isPostgresBlobStorageEnabled());
//            hearingRecording.getDocumentContentVersions().add(documentContentVersion);
//
//            save(hearingRecording);
//            storeInAzureBlobStorage(hearingRecording, documentContentVersion, file);
//            closeBlobInputStream(documentContentVersion);
//            return hearingRecording;
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
//    public DocumentContentVersion addHearingRecordingVersion(HearingRecording hearingRecording, MultipartFile file) {
//        DocumentContentVersion documentContentVersion = new DocumentContentVersion(hearingRecording,
//                                                                                   file,
//                                                                                   securityUtilService.getUserId(),
//                                                                                   azureStorageConfiguration
//                                                                                 .isPostgresBlobStorageEnabled());
//        hearingRecording.getDocumentContentVersions().add(documentContentVersion);
//        documentContentVersionRepository.save(documentContentVersion);
//        storeInAzureBlobStorage(hearingRecording, documentContentVersion, file);
//        closeBlobInputStream(documentContentVersion);
//
//        return documentContentVersion;
//    }
//
//    public void deleteDocument(HearingRecording hearingRecording, boolean permanent) {
//        hearingRecording.setDeleted(true);
//        if (permanent) {
//            hearingRecording.setHardDeleted(true);
//            hearingRecording.getDocumentContentVersions().parallelStream().forEach(documentContentVersion -> {
//                if (azureStorageConfiguration.isAzureBlobStoreEnabled()) {
//                    blobStorageDeleteService.deleteDocumentContentVersion(documentContentVersion);
//                } else if (documentContentVersion.getDocumentContent() != null) {
//                    documentContentRepository.delete(documentContentVersion.getDocumentContent());
//                    documentContentVersion.setDocumentContent(null);
//                }
//            });
//        }
//        hearingRecordingRepository.save(hearingRecording);
//    }
//
//    public void updateHearingRecording(@NonNull HearingRecording hearingRecording,
//    @NonNull UpdateDocumentCommand command) {
//        updateHearingRecording(hearingRecording, command.getTtl(), null);
//    }
//
//    public void updateHearingRecording(
//        @NonNull HearingRecording hearingRecording,
//        Date ttl,
//        Map<String, String> metadata
//    ) {
//        if (hearingRecording.isDeleted()) {
//            return;
//        }
//
//        if (metadata != null) {
//            hearingRecording.getMetadata().putAll(metadata);
//        }
//
//        hearingRecording.setTtl(ttl);
//        hearingRecording.setLastModifiedBy(securityUtilService.getUserId());
//        save(hearingRecording);
//    }
//
//    public List<HearingRecording> findAllExpiredHearingRecordings() {
//        return hearingRecordingRepository.findByTtlLessThanAndHardDeleted(new Date(), false);
//    }
//
//    private void storeInAzureBlobStorage(HearingRecording hearingRecording,
//                                         DocumentContentVersion documentContentVersion,
//                                         MultipartFile file) {
//        if (azureStorageConfiguration.isAzureBlobStoreEnabled()) {
//            blobStorageWriteService.uploadDocumentContentVersion(hearingRecording,
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
