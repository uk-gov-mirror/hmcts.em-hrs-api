package uk.gov.hmcts.reform.em.hrs.service.ccd;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.exception.CcdUploadException;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;
import uk.gov.hmcts.reform.em.hrs.service.FolderService;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CcdUploadServiceImpl implements CcdUploadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CcdUploadServiceImpl.class);

    private final CcdDataStoreApiClient ccdDataStoreApiClient;
    private final HearingRecordingRepository recordingRepository;
    private final HearingRecordingSegmentRepository segmentRepository;
    private final FolderService folderService;

    @Autowired
    public CcdUploadServiceImpl(final CcdDataStoreApiClient ccdDataStoreApiClient,
                                final HearingRecordingRepository recordingRepository,
                                final HearingRecordingSegmentRepository segmentRepository,
                                final FolderService folderService) {
        this.ccdDataStoreApiClient = ccdDataStoreApiClient;
        this.recordingRepository = recordingRepository;
        this.segmentRepository = segmentRepository;
        this.folderService = folderService;
    }

    @Override
    public void upload(final HearingRecordingDto recordingDto) {

        String recordingRef = recordingDto.getRecordingRef();
        String folder = recordingDto.getFolder();

        final Optional<HearingRecording> hearingRecording =
            recordingRepository.findByRecordingRefAndFolderName(recordingRef, folder);

        hearingRecording.ifPresentOrElse(
            x -> updateCase(x, recordingDto),
            () -> createCaseinCcdAndPersist(recordingDto)
        );

    }

    private void updateCase(final HearingRecording recording, final HearingRecordingDto recordingDto) {

        UUID id = recording.getId();
        Long ccdCaseId = recording.getCcdCaseId();
        String recordingRef = recordingDto.getRecordingRef();
        String folder = recordingDto.getFolder();

        if (ccdCaseId == null) {
            LOGGER.info(
                "Recording Ref {} in folder {}, has no ccd id, case still being created in CCD or has been rejected",
                recordingRef, folder
            );
            return;
        }

        LOGGER.info(
            "adding  recording (ref {}) in folder {} to case (ccdId {})", recordingRef, folder, ccdCaseId);


        Long caseDetailsId =
            ccdDataStoreApiClient.updateCaseData(ccdCaseId, id, recordingDto);

        LOGGER.info("Case Details (id {}) updated successfully", caseDetailsId);

        try {
            HearingRecordingSegment segment = createSegment(recording, recordingDto);
            segmentRepository.save(segment);

        } catch (Exception e) {
            LOGGER.info(
                "segment not added to database, probably duplicate entry (ref {}) to case(ccdId {})",
                recordingRef,
                ccdCaseId
            );
        }

        LOGGER.info("updateCase end");
    }

    private void createCaseinCcdAndPersist(final HearingRecordingDto recordingDto) {
        LOGGER.info("creating a new case for recording: {}", recordingDto.getRecordingRef());

        var folder = folderService.getFolderByName(recordingDto.getFolder());

        HearingRecording recording = HearingRecording.builder()
            .folder(folder)
            .recordingRef(recordingDto.getRecordingRef())
            .caseRef(recordingDto.getCaseRef())
            .hearingLocationCode(recordingDto.getCourtLocationCode())
            .hearingRoomRef(recordingDto.getHearingRoomRef())
            .hearingSource(recordingDto.getRecordingSource())
            .jurisdictionCode(recordingDto.getJurisdictionCode())
            .serviceCode(recordingDto.getServiceCode())
            .createdOn(recordingDto.getRecordingDateTime())
            .build();

        try {
            recording = recordingRepository.save(recording);

        } catch (ConstraintViolationException e) {
            //the recording has already been persisted by another cluster - do not proceed as waiting for CCD id
            LOGGER
                .info(
                    "create case Hearing Recording already exists in database, not persisting new recording, nor "
                        + "segment "
                        + "at this time");
            throw new CcdUploadException(
                "Hearing Recording already exists - likely race condition from another server node");
        } catch (Exception e) {
            LOGGER.warn(
                "create case Unhandled Exception whilst adding segment to DB (ref {}) to case(ccdId {})",
                recordingDto.getRecordingRef(),
                recording.getCcdCaseId()
            );
            throw new CcdUploadException("Unhandled Exception trying to persist case");
        }

        LOGGER.info("About to create case in CCD");

        final Long caseId = ccdDataStoreApiClient.createCase(recording.getId(), recordingDto);
        recording.setCcdCaseId(caseId);
        recording = recordingRepository.save(recording);

        HearingRecordingSegment segment = createSegment(recording, recordingDto);
        segmentRepository.save(segment);
    }

    private HearingRecordingSegment createSegment(final HearingRecording recording,
                                                  final HearingRecordingDto recordingDto) {
        return HearingRecordingSegment.builder()
            .filename(recordingDto.getFilename())
            .fileExtension(recordingDto.getFilenameExtension())
            .fileSizeMb(recordingDto.getFileSize())
            .fileMd5Checksum(recordingDto.getCheckSum())
            .ingestionFileSourceUri(recordingDto.getCvpFileUrl())
            .recordingSegment(recordingDto.getSegment())
            .hearingRecording(recording)
            .build();
    }


}
