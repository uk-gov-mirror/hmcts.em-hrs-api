package uk.gov.hmcts.reform.em.hrs.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;


@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class HearingRecordingSharees {

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private HearingRecording hearingRecording;


    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @NotNull
    private String shareeEmail;

    @NotNull
    private String sharedByRef;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date sharedOn;

    public Date getSharedOn() {
        if (sharedOn == null) {
            return null;
        } else {
            return new Date(sharedOn.getTime());
        }
    }

    public void setSharedOn(Date eventDateTime) {
        if (eventDateTime == null) {
            throw new IllegalArgumentException();
        } else {
            this.sharedOn = new Date(eventDateTime.getTime());
        }
    }
}
