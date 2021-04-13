package uk.gov.hmcts.reform.em.hrs.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
    private LocalDateTime modifiedOn;

    @CreatedDate
    private LocalDateTime createdOn;

    public Folder(UUID id,
                  String name,
                  List<HearingRecording> hearingRecordings,
                  List<JobInProgress> jobsInProgress,
                  String createdBy,
                  String lastModifiedBy,
                  LocalDateTime modifiedOn,
                  LocalDateTime createdOn) {
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

    public static class FolderBuilder {
        public FolderBuilder modifiedOn(LocalDateTime modifiedOn) {
            this.modifiedOn = modifiedOn;
            return this;
        }

        public FolderBuilder createdOn(LocalDateTime createdOn) {
            this.createdOn = createdOn;
            return this;
        }
    }

}
