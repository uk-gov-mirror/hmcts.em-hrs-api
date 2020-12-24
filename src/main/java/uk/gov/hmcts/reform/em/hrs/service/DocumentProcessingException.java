package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DocumentProcessingException extends Exception {

    public DocumentProcessingException(String message) {
        super(message);
    }

}
