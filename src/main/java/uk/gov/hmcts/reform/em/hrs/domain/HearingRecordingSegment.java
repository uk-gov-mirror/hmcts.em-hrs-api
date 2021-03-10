package uk.gov.hmcts.reform.em.hrs.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
@Builder
@Getter
@Setter
//@EntityListeners(AuditingEntityListener.class)
public class HearingRecordingSegment {

    @ManyToOne(fetch = FetchType.LAZY)
    private HearingRecording hearingRecording;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    private String createdBy;

    @CreatedBy
    private String createdByService;

    private String lastModifiedBy;

    @LastModifiedBy
    private String lastModifiedByService;

    @LastModifiedDate
    private LocalDateTime modifiedOn;

    @CreatedDate
    private LocalDateTime createdOn;

    private boolean deleted;
    private boolean hardDeleted;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hearingRecordingSegment")
    private Set<HearingRecordingSegmentAuditEntry> auditEntries;

    private String blobUuid; //32 char?

    private String fileName;
    private String fileExtension;
    private String fileMd5Checksum; // char(32),
    private BigDecimal fileSizeMb; // numeric(2),

    private String ingestionFileSourceUri;
    private String segmentIngestionStatus;

    private Integer recordingLengthMins;
    private Integer recordingSegment;

    @NotNull
    private Integer ccdAttachmentId; //TODO check if Integer big enough / if should be a string....

    public HearingRecordingSegment(HearingRecording hearingRecording, UUID id, String createdBy,
                                   String createdByService, String lastModifiedBy,
                                   String lastModifiedByService,
                                   LocalDateTime modifiedOn,
                                   LocalDateTime createdOn,
                                   boolean deleted, boolean hardDeleted,
                                   Set<HearingRecordingSegmentAuditEntry> auditEntries,
                                   String blobUuid, String fileName,
                                   String fileExtension, String fileMd5Checksum, BigDecimal fileSizeMb,
                                   String ingestionFileSourceUri, String segmentIngestionStatus,
                                   Integer recordingLengthMins,
                                   Integer recordingSegment, Integer ccdAttachmentId) {
        setHearingRecording(hearingRecording);
        setId(id);
        setCreatedBy(createdBy);
        setCreatedByService(createdByService);
        this.lastModifiedBy = lastModifiedBy;
        this.setLastModifiedByService(lastModifiedByService);
        setModifiedOn(modifiedOn);
        setCreatedOn(createdOn);
        setDeleted(deleted);
        setHardDeleted(hardDeleted);

        setAuditEntries(auditEntries);
        //setRoles(roles);

        setBlobUuid(blobUuid);
        setFileName(fileName);

        setFileExtension(fileExtension);
        setFileMd5Checksum(fileMd5Checksum);
        setFileSizeMb(fileSizeMb);

        setIngestionFileSourceUri(ingestionFileSourceUri);
        setSegmentIngestionStatus(segmentIngestionStatus);

        setRecordingLengthMins(recordingLengthMins);
        setRecordingSegment(recordingSegment);

        setCcdAttachmentId(ccdAttachmentId);
    }

    public HearingRecordingSegment() {
    }

    public static class HearingRecordingSegmentBuilder {
        public HearingRecordingSegmentBuilder modifiedOn(LocalDateTime modifiedOn) {
            this.modifiedOn = modifiedOn;
            return this;
        }

        public HearingRecordingSegmentBuilder createdOn(LocalDateTime createdOn) {
            this.createdOn = createdOn;
            return this;
        }
    }

}
