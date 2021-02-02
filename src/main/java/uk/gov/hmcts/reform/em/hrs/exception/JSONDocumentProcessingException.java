package uk.gov.hmcts.reform.em.hrs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JSONDocumentProcessingException extends Exception {

    public JSONDocumentProcessingException(String message) {
        super(message);
    }

}
