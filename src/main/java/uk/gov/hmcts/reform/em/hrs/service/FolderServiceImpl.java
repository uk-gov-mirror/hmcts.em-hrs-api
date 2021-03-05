package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.hrs.domain.Folder;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.domain.JobInProgress;
import uk.gov.hmcts.reform.em.hrs.repository.FolderRepository;
import uk.gov.hmcts.reform.em.hrs.storage.HearingRecordingStorage;
import uk.gov.hmcts.reform.em.hrs.util.SetUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@Transactional
public class FolderServiceImpl implements FolderService {
    private final FolderRepository folderRepository;
    private final HearingRecordingStorage hearingRecordingStorage;

    @Inject
    public FolderServiceImpl(final FolderRepository folderRepository,
                             final HearingRecordingStorage hearingRecordingStorage) {
        this.folderRepository = folderRepository;
        this.hearingRecordingStorage = hearingRecordingStorage;
    }

    @Override
    public Optional<Folder> findById(UUID id) {
        return folderRepository.findById(id);
    }

    @Override
    public Set<String> getStoredFiles(String folderName) {
        final Optional<Folder> optionalFolder = folderRepository.findByName(folderName);

        final Set<String> filesInDatabase = optionalFolder.map(x -> getSegmentFilenames(x.getHearingRecordings()))
            .orElse(Collections.emptySet());

        final Set<String> filesInProgress = optionalFolder.map(x -> getFilesInProgress(x.getJobsInProgress()))
            .orElse(Collections.emptySet());

        final Set<String> filesInBlobstore = hearingRecordingStorage.findByFolder(folderName);

        final Set<String> completedFiles = SetUtils.intersect(filesInDatabase, filesInBlobstore);

        return SetUtils.union(filesInProgress, completedFiles);
    }

    @Override
    public void save(Folder folder) {
        folderRepository.save(folder);
    }

    private Set<String> getFilesInProgress(final List<JobInProgress> jobInProgresses) {
        return jobInProgresses.stream()
                .map(JobInProgress::getFilename)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<String> getSegmentFilenames(final List<HearingRecording> hearingRecordings) {
        return hearingRecordings.stream()
            .flatMap(x -> x.getSegments().stream().map(HearingRecordingSegment::getFileName))
            .collect(Collectors.toUnmodifiableSet());
    }
}
