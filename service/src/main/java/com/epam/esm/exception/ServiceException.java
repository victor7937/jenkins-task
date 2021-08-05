package com.epam.esm.exception;

public class ServiceException extends RuntimeException{

    private static final long serialVersionUID = 3038117713318487466L;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Exception e) {
        super(e);
    }

    public ServiceException(String message, Exception e) {
        super(message, e);
    }

}
