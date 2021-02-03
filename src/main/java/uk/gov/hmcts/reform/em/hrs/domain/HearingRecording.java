package uk.gov.hmcts.reform.em.hrs.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class HearingRecording {

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
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedOn;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    private boolean deleted;
    private boolean hardDeleted;

    @ManyToOne
    private Folder folder;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hearingRecording")
    private Set<HearingRecordingAuditEntry> auditEntries;


    //@ElementCollection
    //@CollectionTable(name = "hearing_recording_roles", joinColumns = @JoinColumn(name = "hearing_recording_roles_id"))
    //private Set<String> roles;

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "hearing_recording_metadata",
        joinColumns = @JoinColumn(name = "hearing_recording_metadata_id"))
    private Map<String, String> metadata;


    private Date ttl;

    private String blobUuid; //32 char?

    private String fileName;
    private String fileExtension;
    private String fileMd5Checksum; // char(32),
    private BigDecimal fileSizeMb; // numeric(2),

    private String ingestionFileSourceUri;
    private String ingestionStatus;

    //    @NotNull
    private Integer ingestionRetryCount;

    private String caseReference;
    private String hearingLocationReference;
    private String hearingSource;
    private String jurisdictionCode;
    private String serviceCode;
    private Integer recordingLengthMins;
    private Integer recordingSegment;

    private Integer ccdId; //TODO check if Integer big enough / if should be a string....
    private Integer ccdAttachmentId; //TODO check if Integer big enough / if should be a string....


    public HearingRecording(UUID id, String createdBy, String createdByService, String lastModifiedBy,
                            String lastModifiedByService,
                            Date modifiedOn, Date createdOn,
                            boolean deleted, boolean hardDeleted, Folder folder,
                            Set<HearingRecordingAuditEntry> auditEntries,
                            //Set<String> roles,
                            Map<String, String> metadata, Date ttl, String blobUuid, String fileName,
                            String fileExtension, String fileMd5Checksum, BigDecimal fileSizeMb,
                            String ingestionFileSourceUri, String ingestionStatus, Integer ingestionRetryCount,
                            String caseReference, String hearingLocationReference, String hearingSource,
                            String jurisdictionCode, String serviceCode, Integer recordingLengthMins,
                            Integer recordingSegment, Integer ccdId, Integer ccdAttachmentId) {
        setId(id);
        setCreatedBy(createdBy);
        setCreatedByService(createdByService);
        this.lastModifiedBy = lastModifiedBy;
        this.setLastModifiedByService(lastModifiedByService);
        setModifiedOn(modifiedOn);
        setCreatedOn(createdOn);
        setDeleted(deleted);
        setHardDeleted(hardDeleted);
        setFolder(folder);

        setAuditEntries(auditEntries);
        //setRoles(roles);
        setMetadata(metadata);
        setTtl(ttl);

        setBlobUuid(blobUuid);
        setFileName(fileName);

        setFileExtension(fileExtension);
        setFileMd5Checksum(fileMd5Checksum);
        setFileSizeMb(fileSizeMb);

        setIngestionFileSourceUri(ingestionFileSourceUri);
        setIngestionStatus(ingestionStatus);

        //@NotNull
        setIngestionRetryCount(ingestionRetryCount);

        setCaseReference(caseReference);
        setHearingLocationReference(hearingLocationReference);
        setHearingSource(hearingSource);
        setJurisdictionCode(jurisdictionCode);

        setServiceCode(serviceCode);
        setRecordingLengthMins(recordingLengthMins);
        setRecordingSegment(recordingSegment);

        setCcdId(ccdId);
        ;
        setCcdAttachmentId(ccdAttachmentId);


    }

    public HearingRecording() {

    }

    //TODO shouldn't this field always be not null?
    public Date getModifiedOn() {
        return (modifiedOn == null) ? null : new Date(modifiedOn.getTime());
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = (modifiedOn == null) ? null : new Date(modifiedOn.getTime());
    }

    //TODO shouldn't this field always be not null?
    public Date getCreatedOn() {
        return (createdOn == null) ? null : new Date(createdOn.getTime());
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = (createdOn == null) ? null : new Date(createdOn.getTime());
    }

    public static class HearingRecordingBuilder {
        public HearingRecordingBuilder modifiedOn(Date modifiedOn) {
            this.modifiedOn = (modifiedOn == null) ? null : new Date(modifiedOn.getTime());
            return this;
        }

        public HearingRecordingBuilder createdOn(Date createdOn) {
            this.createdOn = (createdOn == null) ? null : new Date(createdOn.getTime());
            return this;
        }
    }

}
