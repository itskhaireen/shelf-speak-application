package com.example.bookreviewapi.mapper;

import com.example.bookreviewapi.dto.BookDTO;
import com.example.bookreviewapi.dto.CreateBookDTO;
import com.example.bookreviewapi.model.Book;

public class BookMapper {

    // call a static method directly using the class name, without needing to create an instance of the class.

    // Mapped CreatedBookDTO --> Book (Input - POST API)
    // No IDs because it's auto generated in the Book.java (Entity)
    public static Book toEntity(CreateBookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setGenre(dto.getGenre());

        return book;
    }

    // Mapped Book -> BookDTO (Output - GET API)
    public static BookDTO toDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId()); // Users need to see ID for a specific book
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setGenre(book.getGenre());

        return dto;
    }
    
}