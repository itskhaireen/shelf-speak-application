package com.example.bookreviewapi.exception;

public class DatabaseOperationException extends RuntimeException {

    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String operation, Throwable cause) {
        super("Database operation failed: " + operation, cause);
    }
} 