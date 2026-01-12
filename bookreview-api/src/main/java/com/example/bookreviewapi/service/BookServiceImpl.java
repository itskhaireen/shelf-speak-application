package com.example.bookreviewapi.service;

import org.springframework.stereotype.Service;
import com.example.bookreviewapi.repository.BookRepository;
import com.example.bookreviewapi.exception.BookNotFoundException;
import com.example.bookreviewapi.exception.InvalidBookDataException;
import com.example.bookreviewapi.exception.BookAlreadyExistsException;
import com.example.bookreviewapi.exception.DatabaseOperationException;
import com.example.bookreviewapi.model.Book;
import com.example.bookreviewapi.model.Review;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book saveBook(Book book) {
        try {
            // Validate book data
            validateBookData(book);
            
            // Check if book already exists
            checkBookExists(book);
            
            return bookRepository.save(book);
        } catch (RuntimeException e) {
            if (e instanceof InvalidBookDataException || e instanceof BookAlreadyExistsException) {
                throw e; // Re-throw business exceptions
            }
            throw new DatabaseOperationException("save book", e);
        }
    }

    @Override
    public Book getBookByIdOrThrow(Long id) {
        try {
            return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        } catch (BookNotFoundException e) {
            throw e; // Re-throw business exception
        } catch (Exception e) {
            throw new DatabaseOperationException("find book by id", e);
        }
    }

    @Override
    public List<Book> getAllBooks() {
        try {
            return bookRepository.findAll();
        } catch (Exception e) {
            throw new DatabaseOperationException("find all books", e);
        }
    }

    @Override
    public void deleteBook(Long id) {
        try {
            if (!bookRepository.existsById(id)) {
                throw new BookNotFoundException(id);
            }
            bookRepository.deleteById(id);
        } catch (BookNotFoundException e) {
            throw e; // Re-throw business exception
        } catch (Exception e) {
            throw new DatabaseOperationException("delete book", e);
        }
    }

    @Override
    public double getAverageRating(Long bookId) {
        try {
            Book book = bookRepository.findByIdWithReviews(bookId)
                    .orElseThrow(() -> new BookNotFoundException(bookId));
            
            List<Review> reviews = book.getReviews();

            if (reviews == null || reviews.isEmpty()) {
                return 0.0;
            }

            // Calculate the average rating
            double totalRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .sum();

            return totalRating / reviews.size();
        } catch (BookNotFoundException e) {
            throw e; // Re-throw business exception
        } catch (Exception e) {
            throw new DatabaseOperationException("calculate average rating", e);
        }
    }

    private void validateBookData(Book book) {
        if (book == null) {
            throw new InvalidBookDataException("Book cannot be null");
        }
        
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new InvalidBookDataException("Book title cannot be null or empty");
        }
        
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new InvalidBookDataException("Book author cannot be null or empty");
        }
        
        if (book.getTitle().length() > 255) {
            throw new InvalidBookDataException("Book title cannot exceed 255 characters");
        }
        
        if (book.getAuthor().length() > 255) {
            throw new InvalidBookDataException("Book author cannot exceed 255 characters");
        }
    }

    private void checkBookExists(Book book) {
        // Check if a book with the same title and author already exists
        Optional<Book> existingBook = bookRepository.findByTitleAndAuthor(
            book.getTitle(), book.getAuthor());
        
        if (existingBook.isPresent()) {
            throw new BookAlreadyExistsException(book.getTitle(), book.getAuthor());
        }
    }
}