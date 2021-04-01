package uk.gov.hmcts.reform.em.hrs.exception;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    @Getter
    private final String field;
    @Getter
    private final String value;

    public ResourceNotFoundException(@NonNull final String field, @NonNull final String value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public String getMessage() {
        return String.format("Resource with %s: %s could not be found", field, value);
    }

}
