package com.epam.esm.exception;

public class PartialUpdateException extends Exception {

    private static final long serialVersionUID = -6740952715508854912L;

    public PartialUpdateException() {
        super();
    }

    public PartialUpdateException(String message) {
        super(message);
    }

    public PartialUpdateException(Exception e) {
        super(e);
    }

    public PartialUpdateException(String message, Exception e) {
        super(message, e);
    }
}
