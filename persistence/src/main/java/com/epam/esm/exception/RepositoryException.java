package com.epam.esm.exception;

public class RepositoryException extends Exception {

    private static final long serialVersionUID = 2128116873513017122L;

    public RepositoryException() {
        super();
    }

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(Exception e) {
        super(e);
    }

    public RepositoryException(String message, Exception e) {
        super(message, e);
    }
}
