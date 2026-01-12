package com.example.bookreviewapi.service;
import com.example.bookreviewapi.model.Book;

import java.util.List;

public interface BookService {
    
    Book saveBook(Book book);
    Book getBookByIdOrThrow(Long id);
    List<Book> getAllBooks();
    
    void deleteBook(Long id);
    double getAverageRating(Long bookId);

}