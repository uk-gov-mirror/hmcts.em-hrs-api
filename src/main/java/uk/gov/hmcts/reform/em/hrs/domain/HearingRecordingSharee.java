package uk.gov.hmcts.reform.em.hrs.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class HearingRecordingSharee {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private HearingRecording hearingRecording;

    @NotNull
    private String shareeEmail;

    @CreatedBy
    private String sharedByRef;

    @CreatedDate
    private LocalDateTime sharedOn;

    public HearingRecordingSharee() {
    }

    public HearingRecordingSharee(UUID id, HearingRecording hearingRecording, @NotNull String shareeEmail,
                                  String sharedByRef, LocalDateTime sharedOn) {
        this.hearingRecording = hearingRecording;
        this.id = id;
        this.shareeEmail = shareeEmail;
        this.sharedByRef = sharedByRef;
        this.sharedOn = sharedOn;
    }

    public static class HearingRecordingShareeBuilder {
        public HearingRecordingShareeBuilder hearingRecording(HearingRecording hearingRecording) {
            this.hearingRecording = hearingRecording;
            return this;
        }

        public HearingRecordingShareeBuilder shareeEmail(String shareeEmail) {
            this.shareeEmail = shareeEmail;
            return this;
        }
    }
}
