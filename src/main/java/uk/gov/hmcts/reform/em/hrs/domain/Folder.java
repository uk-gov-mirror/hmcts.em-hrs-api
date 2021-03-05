package uk.gov.hmcts.reform.em.hrs.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Builder
@Getter
@Setter
public class Folder {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "folder")
    private List<HearingRecording> hearingRecordings;

    @OneToMany(mappedBy = "folder")
    private List<JobInProgress> jobsInProgress;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;


    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedOn;


    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    public Folder(UUID id,
                  String name,
                  List<HearingRecording> hearingRecordings,
                  List<JobInProgress> jobsInProgress,
                  String createdBy,
                  String lastModifiedBy,
                  Date modifiedOn,
                  Date createdOn) {
        this.id = id;
        this.name = name;
        this.hearingRecordings = hearingRecordings;
        this.jobsInProgress = jobsInProgress;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        setModifiedOn(modifiedOn);
        setCreatedOn(createdOn);
    }

    public Folder() {
        hearingRecordings = new ArrayList<>();
        jobsInProgress = new ArrayList<>();
    }

    public Date getModifiedOn() {
        return (modifiedOn == null) ? null : new Date(modifiedOn.getTime());
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = (modifiedOn == null) ? null : new Date(modifiedOn.getTime());
    }

    public Date getCreatedOn() {
        return (createdOn == null) ? null : new Date(createdOn.getTime());
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = (createdOn == null) ? null : new Date(createdOn.getTime());
    }

    public static class FolderBuilder {
        public FolderBuilder modifiedOn(Date modifiedOn) {
            this.modifiedOn = (modifiedOn == null) ? null : new Date(modifiedOn.getTime());
            return this;
        }

        public FolderBuilder createdOn(Date createdOn) {
            this.createdOn = (createdOn == null) ? null : new Date(createdOn.getTime());
            return this;
        }
    }

}
