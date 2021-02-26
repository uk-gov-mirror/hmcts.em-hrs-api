package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.domain.JobInProgress;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;
import uk.gov.hmcts.reform.em.hrs.repository.JobInProgressRepository;
import uk.gov.hmcts.reform.em.hrs.storage.HearingRecordingStorage;
import uk.gov.hmcts.reform.em.hrs.utils.SetUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class HearingRecordingServiceImpl implements HearingRecordingService {
    private final JobInProgressRepository jobInProgressRepository;
    private final HearingRecordingRepository hearingRecordingRepository;
    private final HearingRecordingStorage hearingRecordingStorage;

    @Inject
    public HearingRecordingServiceImpl(final JobInProgressRepository jobInProgressRepository,
                                       final HearingRecordingRepository hearingRecordingRepository,
                                       final HearingRecordingStorage hearingRecordingStorage) {
        this.jobInProgressRepository = jobInProgressRepository;
        this.hearingRecordingRepository = hearingRecordingRepository;
        this.hearingRecordingStorage = hearingRecordingStorage;
    }

    @Override
    public Set<String> getStoredFiles(final String folderName) {
        final Set<String> filesInProgress = getFilesInProgress(folderName);
        final Set<String> completedFiles = getCompletedFiles(folderName);

        return SetUtils.union(filesInProgress, completedFiles);
    }

    private Set<String> getFilesInProgress(final String folderName) {
        final List<JobInProgress> jobsInProgress = jobInProgressRepository.findByFolder(folderName);
        return jobsInProgress.stream()
            .map(JobInProgress::getFilename)
            .collect(Collectors.toUnmodifiableSet());
    }

    private Set<String> getCompletedFiles(final String folderName) {
        final List<HearingRecording> segments = hearingRecordingRepository.findByFolder(folderName);
        final Set<String> filesInDatabase = segments.stream()
            .flatMap(x -> x.getSegments().stream().map(HearingRecordingSegment::getFileName))
            .collect(Collectors.toUnmodifiableSet());

        final Set<String> filesInBlobstore = hearingRecordingStorage.findByFolder(folderName);

        return SetUtils.intersect(filesInDatabase, filesInBlobstore);
    }
}
