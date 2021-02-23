package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.hrs.domain.Folder;
import uk.gov.hmcts.reform.em.hrs.repository.FolderRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Named
public class FolderService {
    private final FolderRepository folderRepository;

    @Inject
    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public Optional<Folder> findById(UUID id) {
        return folderRepository.findById(id);
    }

    public void save(Folder folder) {
        folderRepository.save(folder);
    }

    public void delete(UUID id) {
        delete(findById(id));
    }

    public void delete(Optional<Folder> maybeFolder) {
        maybeFolder.ifPresent(folder -> folderRepository.delete(folder));
    }


}
