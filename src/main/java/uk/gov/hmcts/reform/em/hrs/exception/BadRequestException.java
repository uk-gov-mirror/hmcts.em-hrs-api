package uk.gov.hmcts.reform.em.hrs.exception;

import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

@Getter
public abstract class BadRequestException extends RuntimeException {

    private final Map<String, Object> data;

    public BadRequestException(final String data) {
        this.data = data;
    }

}
