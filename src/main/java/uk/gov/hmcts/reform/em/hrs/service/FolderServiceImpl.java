package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.transaction.annotation.Transactional;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import uk.gov.hmcts.reform.em.hrs.domain.Folder;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.domain.JobInProgress;
import uk.gov.hmcts.reform.em.hrs.repository.FolderRepository;
import uk.gov.hmcts.reform.em.hrs.repository.JobInProgressRepository;
import uk.gov.hmcts.reform.em.hrs.storage.HearingRecordingStorage;
import uk.gov.hmcts.reform.em.hrs.util.SetUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

@Named
@Transactional
public class FolderServiceImpl implements FolderService {
    private final FolderRepository folderRepository;
    private final JobInProgressRepository jobInProgressRepository;
    private final HearingRecordingStorage hearingRecordingStorage;

    @Inject
    public FolderServiceImpl(final FolderRepository folderRepository,
                             final JobInProgressRepository jobInProgressRepository,
                             final HearingRecordingStorage hearingRecordingStorage) {
        this.folderRepository = folderRepository;
        this.jobInProgressRepository = jobInProgressRepository;
        this.hearingRecordingStorage = hearingRecordingStorage;
    }

    @Override
    public Set<String> getStoredFiles(final String folderName) {
        deleteStaledJobs();

        Optional<Folder> optionalFolder = folderRepository.findByName(folderName);

        //create folder in database if not exists, and return empty set of files
        if (optionalFolder.isEmpty()) {
            Folder newFolder = Folder.builder().name(folderName).build();
            folderRepository.save(newFolder);
            return Collections.emptySet();
        }

        return getCompletedAndInProgressFiles(optionalFolder.get());
    }

    private String getFolderNameFromFilePath(@NotNull final String path) {
        final int separatorIndex = path.indexOf("/");
        return path.substring(0, separatorIndex);
    }

    @Override
    public Folder getFolderFromFilePath(@NotNull final String path) {
        final String folderName = getFolderNameFromFilePath(path);
        Optional<Folder> folder = folderRepository.findByName(folderName);
        return folder.get();
    }

    private Set<String> getCompletedAndInProgressFiles(final Folder optionalFolder) {

        final Tuple2<FilesInDatabase, Set<String>> databaseRecords = getFilesetsFromDatabase(optionalFolder);
        final FilesInDatabase filesInDatabase = databaseRecords.getT1();

        final Set<String> filesInBlobstore = hearingRecordingStorage.findByFolder(optionalFolder.getName());

        final Set<String> completedFiles = filesInDatabase.intersect(filesInBlobstore);
        final Set<String> filesInProgress = databaseRecords.getT2();

        return SetUtils.union(completedFiles, filesInProgress);
    }

    private Tuple2<FilesInDatabase, Set<String>> getFilesetsFromDatabase(final Folder folder) {

        final Set<String> filesInDatabase = getSegmentFilenames(folder.getHearingRecordings());
        final Set<String> filesInProgress = getFilesInProgress(folder.getJobsInProgress());

        return Tuples.of(new FilesInDatabase(filesInDatabase), filesInProgress);
    }

    private Set<String> getFilesInProgress(final List<JobInProgress> jobInProgresses) {
        return jobInProgresses.stream()
            .map(JobInProgress::getFilename)
            .collect(Collectors.toUnmodifiableSet());
    }

    private void deleteStaledJobs() {
        final LocalDateTime yesterday = LocalDateTime.now(Clock.systemUTC()).minusHours(24);
        jobInProgressRepository.deleteByCreatedOnLessThan(yesterday);
    }

    private Set<String> getSegmentFilenames(final List<HearingRecording> hearingRecordings) {
        return hearingRecordings.stream()
            .flatMap(x -> x.getSegments().stream().map(HearingRecordingSegment::getFilename))
            .collect(Collectors.toUnmodifiableSet());
    }

    static class FilesInDatabase {
        private final Set<String> fileset;

        public FilesInDatabase(Set<String> fileset) {
            this.fileset = fileset;
        }

        public Set<String> intersect(final Set<String> filesInBlobstore) {
            return SetUtils.intersect(fileset, filesInBlobstore);
        }
    }
}
