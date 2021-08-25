package uk.gov.hmcts.reform.em.hrs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.storage.HearingRecordingStorage;
import uk.gov.hmcts.reform.em.hrs.util.CcdUploadQueue;

@Service
@Transactional
public class IngestionServiceImpl implements IngestionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IngestionServiceImpl.class);

    private final HearingRecordingStorage hearingRecordingStorage;
    private final CcdUploadQueue ccdUploadQueue= CcdUploadQueue.builder().build();

    @Autowired
    public IngestionServiceImpl(final HearingRecordingStorage hearingRecordingStorage) {
        this.hearingRecordingStorage = hearingRecordingStorage;
    }

    @Override
    @Async("HrsAsyncExecutor")
    public void ingest(final HearingRecordingDto hearingRecordingDto) {
        String cvpFileUrl = hearingRecordingDto.getCvpFileUrl();
        String filename = hearingRecordingDto.getFilename();

        hearingRecordingStorage.copyRecording(cvpFileUrl, filename);
        ccdUploadQueue.offer(hearingRecordingDto);
    }

}
