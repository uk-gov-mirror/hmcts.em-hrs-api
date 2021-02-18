package uk.gov.hmcts.reform.em.hrs.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue(value = "hearing_recording_segment")
public class HearingRecordingSegmentAuditEntry extends AuditEntry {

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private HearingRecordingSegment hearingRecordingSegment;

}
