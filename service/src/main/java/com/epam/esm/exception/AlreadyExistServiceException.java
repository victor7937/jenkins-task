package com.epam.esm.exception;

public class AlreadyExistServiceException extends ServiceException{

    private static final long serialVersionUID = 8404950671065059546L;

    public AlreadyExistServiceException() {
        super();
    }

    public AlreadyExistServiceException(String message) {
        super(message);
    }

    public AlreadyExistServiceException(Exception e) {
        super(e);
    }

    public AlreadyExistServiceException(String message, Exception e) {
        super(message, e);
    }

}
