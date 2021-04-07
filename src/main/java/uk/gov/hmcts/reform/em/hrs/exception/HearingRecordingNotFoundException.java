package uk.gov.hmcts.reform.em.hrs.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class HearingRecordingNotFoundException extends ResourceNotFoundException {

    public HearingRecordingNotFoundException(@NonNull Long ccdCaseId) {
        super("CcdCaseId", ccdCaseId.toString());
    }

    @Override
    public String getMessage() {
        return String.format("Hearing Recording with %s: %s could not be found", getField(), getValue());
    }

}
