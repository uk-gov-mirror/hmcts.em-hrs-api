package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.domain.Folder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface FolderService {
    Optional<Folder> findById(UUID id);

    Set<String> getStoredFiles(String folderName);

    void save(Folder folder);
}
