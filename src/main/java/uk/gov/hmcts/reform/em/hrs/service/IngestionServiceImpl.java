package uk.gov.hmcts.reform.em.hrs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.storage.HearingRecordingStorage;

import java.util.concurrent.LinkedBlockingQueue;

@Service
@Transactional
public class IngestionServiceImpl implements IngestionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IngestionServiceImpl.class);

    private final HearingRecordingStorage hearingRecordingStorage;
    private final LinkedBlockingQueue<HearingRecordingDto> ccdUploadQueue;

    @Autowired
    public IngestionServiceImpl(final HearingRecordingStorage hearingRecordingStorage, @Qualifier("ccdUploadQueue")
    final LinkedBlockingQueue<HearingRecordingDto> ccdUploadQueue) {
        this.hearingRecordingStorage = hearingRecordingStorage;
        this.ccdUploadQueue = ccdUploadQueue;
    }

    @Override
    @Async("HrsAsyncExecutor")
    public void ingest(final HearingRecordingDto hearingRecordingDto) {
        String cvpFileUrl = hearingRecordingDto.getCvpFileUrl();
        String filename = hearingRecordingDto.getFilename();

        hearingRecordingStorage.copyRecording(cvpFileUrl, filename);
        boolean accepted = ccdUploadQueue.offer(hearingRecordingDto);

        if (!accepted) {
            LOGGER.warn("CCD Upload Queue Full. Not uploading file: {} " + hearingRecordingDto.getFilename());
        }
    }

}
