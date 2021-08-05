package com.epam.esm.exception;

public class NotFoundServiceException extends ServiceException{

    private static final long serialVersionUID = -3923578244598069719L;

    public NotFoundServiceException() {
        super();
    }

    public NotFoundServiceException(String message) {
        super(message);
    }

    public NotFoundServiceException(Exception e) {
        super(e);
    }

    public NotFoundServiceException(String message, Exception e) {
        super(message, e);
    }

}
