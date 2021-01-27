package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DocumentProcessingException extends Exception {
    private static final long serialVersionUID = 1L;

    public DocumentProcessingException(String message) {
        super(message);
    }

}
