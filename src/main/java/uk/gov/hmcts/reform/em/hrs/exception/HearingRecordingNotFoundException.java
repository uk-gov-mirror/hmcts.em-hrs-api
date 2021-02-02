package uk.gov.hmcts.reform.em.hrs.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class HearingRecordingNotFoundException extends ResourceNotFoundException {

    public HearingRecordingNotFoundException(@NonNull UUID uuid) {
        super(uuid);
    }

    @Override
    public String getMessage() {
        return String.format("Hearing Recording with ID: %s could not be found", getUuid().toString());
    }

}
