package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;
import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingSegmentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;


@Named
@Service
@Transactional
public class HearingRecordingSegmentServiceImpl implements HearingRecordingSegmentService {

    private final HearingRecordingSegmentRepository segmentRepository;
    private final HearingRecordingRepository recordingRepository;

    @Inject
    public HearingRecordingSegmentServiceImpl(
        final HearingRecordingSegmentRepository segmentRepository, HearingRecordingRepository recordingRepository) {
        this.segmentRepository = segmentRepository;
        this.recordingRepository = recordingRepository;
    }

    public List<HearingRecordingSegment> findByRecordingId(UUID id) {
        return segmentRepository.findByRecordingId(id);
    }

    @Override
    public HearingRecordingSegment createAndSaveEntry(
        HearingRecordingSegment hearingRecordingSegment) {
        segmentRepository.save(hearingRecordingSegment);
        return hearingRecordingSegment;

    }

    @Override
    public HearingRecordingSegment persistRecording(final HearingRecordingDto hearingRecordingDto,
                                             final Optional<HearingRecording> existingRecording,
                                             final Long caseId) {

        HearingRecordingSegment.HearingRecordingSegmentBuilder segmentBuilder = HearingRecordingSegment.builder()
            .filename(hearingRecordingDto.getFilename())
            .fileExtension(hearingRecordingDto.getFilenameExtension())
            .fileSizeMb(hearingRecordingDto.getFileSize())
            .fileMd5Checksum(hearingRecordingDto.getCheckSum())
            .ingestionFileSourceUri(hearingRecordingDto.getCvpFileUrl())
            .recordingSegment(hearingRecordingDto.getSegment());

        if (existingRecording.isPresent()) {
            segmentBuilder.hearingRecording(existingRecording.get());
        } else {
            HearingRecording recording = HearingRecording.builder()
                .recordingRef(hearingRecordingDto.getRecordingRef())
                .ccdCaseId(caseId)
                .caseRef(hearingRecordingDto.getCaseRef())
                .hearingLocationCode(hearingRecordingDto.getCourtLocationCode())
                .hearingRoomRef(hearingRecordingDto.getHearingRoomRef())
                .hearingSource(hearingRecordingDto.getRecordingSource())
                .jurisdictionCode(hearingRecordingDto.getJurisdictionCode())
                .serviceCode(hearingRecordingDto.getServiceCode())
                .build();
            recordingRepository.save(recording);
            segmentBuilder.hearingRecording(recording);
        }
        return segmentRepository.save(segmentBuilder.build());
    }
}
