package uk.gov.hmcts.reform.em.hrs.exception;

public class RepositoryCouldNotBeFoundException extends RuntimeException  {

    public RepositoryCouldNotBeFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryCouldNotBeFoundException(String message) {
        super(message);
    }
}
