package com.epam.esm.message;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;


/**
 * Message that contains exception or error data
 */
@Data
@NoArgsConstructor
public class ResponseExceptionMessage implements Serializable {

    private static final long serialVersionUID = -1513236421502919101L;

    private int statusCode;

    private HttpStatus status;

    private String message;

    public ResponseExceptionMessage(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.statusCode = status.value();
    }

    public ResponseExceptionMessage(int statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
        this.status = HttpStatus.valueOf(statusCode / 10);
    }
}
