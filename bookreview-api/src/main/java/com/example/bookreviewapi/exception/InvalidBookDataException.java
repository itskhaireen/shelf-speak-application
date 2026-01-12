package com.example.bookreviewapi.exception;

public class InvalidBookDataException extends RuntimeException {

    public InvalidBookDataException(String message) {
        super(message);
    }

    public InvalidBookDataException(String message, Throwable cause) {
        super(message, cause);
    }
} 