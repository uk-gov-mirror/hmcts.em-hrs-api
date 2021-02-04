//package uk.gov.hmcts.reform.em.hrs.domain;

//TODO


//
//import lombok.Getter;
//import lombok.Setter;
//import org.hibernate.annotations.GenericGenerator;
//
//import java.util.Date;
//import java.util.UUID;
//import javax.persistence.Entity;
//import javax.persistence.EnumType;
//import javax.persistence.Enumerated;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.Inheritance;
//import javax.persistence.InheritanceType;
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//import javax.validation.constraints.NotNull;
//
//
//@Entity
//@Getter
//@Setter
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//public class HearingRecordingSharees {
//
//    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    private UUID id;
//
//    @NotNull
//    @Enumerated(EnumType.STRING)
//    private AuditActions action;
//
//    private String username;
//
//    @NotNull
//    private String serviceName;
//
//    @NotNull
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date recordedDateTime;
//
//    public Date getRecordedDateTime() {
//        if (recordedDateTime == null) {
//            return null;
//        } else {
//            return new Date(recordedDateTime.getTime());
//        }
//    }
//
//    public void setRecordedDateTime(Date recordedDateTime) {
//        if (recordedDateTime == null) {
//            throw new IllegalArgumentException();
//        } else {
//            this.recordedDateTime = new Date(recordedDateTime.getTime());
//        }
//    }
//}
