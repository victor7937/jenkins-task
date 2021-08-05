package com.epam.esm.exception;

public class IncorrectDataServiceException extends ServiceException{

    private static final long serialVersionUID = 284848326733923242L;

    public IncorrectDataServiceException() {
        super();
    }

    public IncorrectDataServiceException(String message) {
        super(message);
    }

    public IncorrectDataServiceException(Exception e) {
        super(e);
    }

    public IncorrectDataServiceException(String message, Exception e) {
        super(message, e);
    }
}
