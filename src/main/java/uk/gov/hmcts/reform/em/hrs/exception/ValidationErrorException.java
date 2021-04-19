package uk.gov.hmcts.reform.em.hrs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;import lombok.NonNull;import java.util.Map;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationErrorException extends BadRequestException {

    public ValidationErrorException(final String data) {
        super(data);
    }

}
