package uk.gov.hmcts.reform.em.hrs.service;

import java.util.Set;

public interface FolderService {
    Set<String> getStoredFiles(String folderName);
}
