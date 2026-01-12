package com.example.bookreviewapi.exception;

public class BookAlreadyExistsException extends RuntimeException {

    public BookAlreadyExistsException(String message) {
        super(message);
    }

    public BookAlreadyExistsException(String title, String author) {
        super("Book already exists with title: '" + title + "' and author: '" + author + "'");
    }
} 