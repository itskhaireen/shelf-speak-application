package com.example.bookreviewapi.service;

import com.example.bookreviewapi.exception.BookNotFoundException;
import com.example.bookreviewapi.exception.InvalidBookDataException;
import com.example.bookreviewapi.exception.BookAlreadyExistsException;
import com.example.bookreviewapi.exception.DatabaseOperationException;
import com.example.bookreviewapi.model.Book;
import com.example.bookreviewapi.model.Review;
import com.example.bookreviewapi.repository.BookRepository;
import com.example.bookreviewapi.repository.ReviewRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock   
    private BookRepository bookRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void saveBook_shouldReturnSavedBook() {
        // Arrange (Prepare data and mock behavior)
        Book inputBook = new Book();
        inputBook.setId(2L);
        inputBook.setTitle("Test Effective Java");
        inputBook.setAuthor("Putri Khairi");

        when(bookRepository.save(any(Book.class))).thenReturn(inputBook);

        // Act (Call the method to be tested)
        Book savedBook = bookService.saveBook(inputBook);

        // Assert (Verify the results)
        assertNotNull(savedBook);
        assertEquals(2L, savedBook.getId());
        assertEquals("Test Effective Java", savedBook.getTitle());
        assertEquals("Putri Khairi", savedBook.getAuthor());


        // This proves service delegated the call to the repository properly.
        verify(bookRepository, times(1)).save(inputBook);
    }

    @Test
    void saveBook_whenRepositoryThrowsException_shouldPropagateException() {
        // Arrange
        Book inputBook = new Book();
        inputBook.setTitle("Test Book");
        inputBook.setAuthor("Test Author");
        
        when(bookRepository.findByTitleAndAuthor(anyString(), anyString())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class)))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        // This will be caught and wrapped in DatabaseOperationException
        assertThrows(DatabaseOperationException.class, () -> bookService.saveBook(inputBook));
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByTitleAndAuthor("Test Book", "Test Author");
        verify(bookRepository, times(1)).save(inputBook);
    }

    @Test
    void saveBook_whenBookIsNull_shouldThrowInvalidBookDataException() {
        // Act & Assert
        assertThrows(InvalidBookDataException.class, () -> bookService.saveBook(null));
        
        // Verify repository was not called
        verify(bookRepository, never()).findByTitleAndAuthor(anyString(), anyString());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void saveBook_whenTitleIsNull_shouldThrowInvalidBookDataException() {
        // Arrange
        Book inputBook = new Book();
        inputBook.setTitle(null);
        inputBook.setAuthor("Test Author");

        // Act & Assert
        assertThrows(InvalidBookDataException.class, () -> bookService.saveBook(inputBook));
        
        // Verify repository was not called
        verify(bookRepository, never()).findByTitleAndAuthor(anyString(), anyString());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void saveBook_whenTitleIsEmpty_shouldThrowInvalidBookDataException() {
        // Arrange
        Book inputBook = new Book();
        inputBook.setTitle("");
        inputBook.setAuthor("Test Author");

        // Act & Assert
        assertThrows(InvalidBookDataException.class, () -> bookService.saveBook(inputBook));
        
        // Verify repository was not called
        verify(bookRepository, never()).findByTitleAndAuthor(anyString(), anyString());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void saveBook_whenAuthorIsNull_shouldThrowInvalidBookDataException() {
        // Arrange
        Book inputBook = new Book();
        inputBook.setTitle("Test Book");
        inputBook.setAuthor(null);

        // Act & Assert
        assertThrows(InvalidBookDataException.class, () -> bookService.saveBook(inputBook));
        
        // Verify repository was not called
        verify(bookRepository, never()).findByTitleAndAuthor(anyString(), anyString());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void saveBook_whenAuthorIsEmpty_shouldThrowInvalidBookDataException() {
        // Arrange
        Book inputBook = new Book();
        inputBook.setTitle("Test Book");
        inputBook.setAuthor("");

        // Act & Assert
        assertThrows(InvalidBookDataException.class, () -> bookService.saveBook(inputBook));
        
        // Verify repository was not called
        verify(bookRepository, never()).findByTitleAndAuthor(anyString(), anyString());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void saveBook_whenTitleTooLong_shouldThrowInvalidBookDataException() {
        // Arrange
        Book inputBook = new Book();
        inputBook.setTitle("A".repeat(256)); // 256 characters
        inputBook.setAuthor("Test Author");

        // Act & Assert
        assertThrows(InvalidBookDataException.class, () -> bookService.saveBook(inputBook));
        
        // Verify repository was not called
        verify(bookRepository, never()).findByTitleAndAuthor(anyString(), anyString());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void saveBook_whenAuthorTooLong_shouldThrowInvalidBookDataException() {
        // Arrange
        Book inputBook = new Book();
        inputBook.setTitle("Test Book");
        inputBook.setAuthor("A".repeat(256)); // 256 characters

        // Act & Assert
        assertThrows(InvalidBookDataException.class, () -> bookService.saveBook(inputBook));
        
        // Verify repository was not called
        verify(bookRepository, never()).findByTitleAndAuthor(anyString(), anyString());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void saveBook_whenBookAlreadyExists_shouldThrowBookAlreadyExistsException() {
        // Arrange
        Book inputBook = new Book();
        inputBook.setTitle("Test Book");
        inputBook.setAuthor("Test Author");
        
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Test Book");
        existingBook.setAuthor("Test Author");
        
        when(bookRepository.findByTitleAndAuthor("Test Book", "Test Author"))
            .thenReturn(Optional.of(existingBook));

        // Act & Assert
        assertThrows(BookAlreadyExistsException.class, () -> bookService.saveBook(inputBook));
        
        // Verify repository was called for duplicate check but not for save
        verify(bookRepository, times(1)).findByTitleAndAuthor("Test Book", "Test Author");
        verify(bookRepository, never()).save(any());
    }

    @Test
    void saveBook_whenValidBookAndNoDuplicate_shouldSaveSuccessfully() {
        // Arrange
        Book inputBook = new Book();
        inputBook.setTitle("Test Book");
        inputBook.setAuthor("Test Author");
        
        when(bookRepository.findByTitleAndAuthor("Test Book", "Test Author"))
            .thenReturn(Optional.empty());
        when(bookRepository.save(inputBook)).thenReturn(inputBook);

        // Act
        Book savedBook = bookService.saveBook(inputBook);

        // Assert
        assertNotNull(savedBook);
        assertEquals("Test Book", savedBook.getTitle());
        assertEquals("Test Author", savedBook.getAuthor());
        
        // Verify repository calls
        verify(bookRepository, times(1)).findByTitleAndAuthor("Test Book", "Test Author");
        verify(bookRepository, times(1)).save(inputBook);
    }

    @Test
    void getBookByIdOrThrow_whenBookExists_shouldReturnBook() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Architecture");
        
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act (Call the actual method to be tested)
        Book result = bookService.getBookByIdOrThrow(1L);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Clean Architecture", result.getTitle());
    }

    @Test
    void getBookByIdOrThrow_whenBookDoesNotExist_shouldThrowException() {
        // Arrange -- no mock data since the book does not exist
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.getBookByIdOrThrow(99L));

        // Verify that the repository was called
        verify(bookRepository, times(1)).findById(99L);

    }

    @Test
    void getAllBooks_ShouldReturnAllBooks() {
        // Arrange and prepare data > 1 since its a list of books
        Book book1 = new Book();
        book1.setId(34L);
        book1.setTitle("Life On Earth");
        book1.setAuthor("Ellis Wang");
        book1.setGenre("Science Fiction");

        Book book2 = new Book();
        book2.setId(23L);
        book2.setTitle("Sherlock Holmes");
        book2.setAuthor("Arthur Conan Doyle");
        book2.setGenre("Classic Literature");

        // Create the list
        List<Book> mockBook = List.of(book1, book2);

        when(bookRepository.findAll()).thenReturn(mockBook);
        
        // Act (Call the actual method to be tested)
        List<Book> result = bookService.getAllBooks();

        // Assert - Compare the size and contents of the list
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Life On Earth", result.get(0).getTitle());
        assertEquals("Sherlock Holmes", result.get(1).getTitle());

        assertEquals("Ellis Wang", result.get(0).getAuthor());
        assertEquals("Arthur Conan Doyle", result.get(1).getAuthor());

        assertEquals("Science Fiction", result.get(0).getGenre());
        assertEquals("Classic Literature", result.get(1).getGenre());

        // Verify that the repository was called once
        verify(bookRepository, times(1)).findAll();

    }

    @Test
    void getAllBooks_whenNoBooksExist_shouldReturnEmptyList() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(List.of());
        
        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        // Verify repository was called
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getAllBooks_whenRepositoryThrowsException_shouldPropagateException() {
        // Arrange
        when(bookRepository.findAll())
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        assertThrows(DatabaseOperationException.class, () -> bookService.getAllBooks());
        
        // Verify repository was called
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void deleteBook_whenBookExists_shouldDeleteSuccessfully() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(bookId);

        // Act
        bookService.deleteBook(bookId);

        // Assert
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void deleteBook_whenBookDoesNotExist_shouldThrowBookNotFoundException() {
        // Arrange
        Long bookId = 999L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(bookId));
        
        // Verify existsById was called but deleteById was not
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, never()).deleteById(bookId);
    }

    @Test
    void deleteBook_whenExistsByIdThrowsException_shouldPropagateException() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.existsById(bookId))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        assertThrows(DatabaseOperationException.class, () -> bookService.deleteBook(bookId));
        
        // Verify existsById was called but deleteById was not
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, never()).deleteById(bookId);
    }

    @Test
    void deleteBook_whenDeleteByIdThrowsException_shouldPropagateException() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);
        doThrow(new RuntimeException("Delete operation failed"))
            .when(bookRepository).deleteById(bookId);

        // Act & Assert
        assertThrows(DatabaseOperationException.class, () -> bookService.deleteBook(bookId));
        
        // Verify both methods were called
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void getAverageRating_whenBookExistsWithReviews_shouldReturnCorrectAverage() {
        // Arrange
        Long bookId = 1L;
        
        // Create reviews
        Review review1 = new Review();
        review1.setId(1L);
        review1.setRating(5);
        
        Review review2 = new Review();
        review2.setId(2L);
        review2.setRating(3);
        
        Review review3 = new Review();
        review3.setId(3L);
        review3.setRating(4);
        
        // Create book with reviews
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setReviews(List.of(review1, review2, review3));
        
        when(bookRepository.findByIdWithReviews(bookId)).thenReturn(Optional.of(book));
        
        // Act
        double result = bookService.getAverageRating(bookId);
        
        // Assert
        // Expected: (5 + 3 + 4) / 3 = 12 / 3 = 4.0
        assertEquals(4.0, result, 0.01);
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }
    
    @Test
    void getAverageRating_whenBookExistsButNoReviews_shouldReturnZero() {
        // Arrange
        Long bookId = 1L;
        
        // Create book with no reviews
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setReviews(new ArrayList<>());
        
        when(bookRepository.findByIdWithReviews(bookId)).thenReturn(Optional.of(book));
        
        // Act
        double result = bookService.getAverageRating(bookId);
        
        // Assert
        assertEquals(0.0, result, 0.01);
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }
    
    @Test
    void getAverageRating_whenBookExistsButReviewsIsNull_shouldReturnZero() {
        // Arrange
        Long bookId = 1L;
        
        // Create book with null reviews
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setReviews(null);
        
        when(bookRepository.findByIdWithReviews(bookId)).thenReturn(Optional.of(book));
        
        // Act
        double result = bookService.getAverageRating(bookId);
        
        // Assert
        assertEquals(0.0, result, 0.01);
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }
    
    @Test
    void getAverageRating_whenBookDoesNotExist_shouldThrowBookNotFoundException() {
        // Arrange
        Long bookId = 999L;
        
        when(bookRepository.findByIdWithReviews(bookId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.getAverageRating(bookId));
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }
    
    @Test
    void getAverageRating_whenBookHasSingleReview_shouldReturnThatRating() {
        // Arrange
        Long bookId = 1L;
        Review review = new Review();
        review.setId(1L);
        review.setRating(5);
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        review.setBook(book); // Ensure review is associated with the book
        book.setReviews(List.of(review));
        when(bookRepository.findByIdWithReviews(bookId)).thenReturn(Optional.of(book));
        // Act
        double result = bookService.getAverageRating(bookId);
        // Assert
        assertEquals(5.0, result, 0.01);
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }
    
    @Test
    void getAverageRating_whenBookHasReviewsWithDecimalResult_shouldReturnCorrectAverage() {
        // Arrange
        Long bookId = 1L;
        
        // Create reviews that will result in decimal average
        Review review1 = new Review();
        review1.setId(1L);
        review1.setRating(3);
        
        Review review2 = new Review();
        review2.setId(2L);
        review2.setRating(4);
        
        Review review3 = new Review();
        review3.setId(3L);
        review3.setRating(5);
        
        Review review4 = new Review();
        review4.setId(4L);
        review4.setRating(2);
        
        // Create book with reviews
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setReviews(List.of(review1, review2, review3, review4));
        
        when(bookRepository.findByIdWithReviews(bookId)).thenReturn(Optional.of(book));
        
        // Act
        double result = bookService.getAverageRating(bookId);
        
        // Assert
        // Expected: (3 + 4 + 5 + 2) / 4 = 14 / 4 = 3.5
        assertEquals(3.5, result, 0.01);
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }

    @Test
    void getAverageRating_whenBookHasReviewsWithZeroRatings_shouldReturnZero() {
        // Arrange
        Long bookId = 1L;
        
        Review review1 = new Review();
        review1.setId(1L);
        review1.setRating(0);
        
        Review review2 = new Review();
        review2.setId(2L);
        review2.setRating(0);
        
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setReviews(List.of(review1, review2));
        
        when(bookRepository.findByIdWithReviews(bookId)).thenReturn(Optional.of(book));
        
        // Act
        double result = bookService.getAverageRating(bookId);
        
        // Assert
        assertEquals(0.0, result, 0.01);
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }

    @Test
    void getAverageRating_whenBookHasReviewsWithMaximumRatings_shouldReturnCorrectAverage() {
        // Arrange
        Long bookId = 1L;
        
        Review review1 = new Review();
        review1.setId(1L);
        review1.setRating(5);
        
        Review review2 = new Review();
        review2.setId(2L);
        review2.setRating(5);
        
        Review review3 = new Review();
        review3.setId(3L);
        review3.setRating(5);
        
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setReviews(List.of(review1, review2, review3));
        
        when(bookRepository.findByIdWithReviews(bookId)).thenReturn(Optional.of(book));
        
        // Act
        double result = bookService.getAverageRating(bookId);
        
        // Assert
        assertEquals(5.0, result, 0.01);
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }

    @Test
    void getAverageRating_whenBookHasReviewsWithMinimumRatings_shouldReturnCorrectAverage() {
        // Arrange
        Long bookId = 1L;
        
        Review review1 = new Review();
        review1.setId(1L);
        review1.setRating(1);
        
        Review review2 = new Review();
        review2.setId(2L);
        review2.setRating(1);
        
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setReviews(List.of(review1, review2));
        
        when(bookRepository.findByIdWithReviews(bookId)).thenReturn(Optional.of(book));
        
        // Act
        double result = bookService.getAverageRating(bookId);
        
        // Assert
        assertEquals(1.0, result, 0.01);
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }

    @Test
    void getAverageRating_whenBookHasManyReviews_shouldHandleLargeDataset() {
        // Arrange
        Long bookId = 1L;
        
        // Create 100 reviews with rating 3
        List<Review> reviews = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Review review = new Review();
            review.setId((long) i);
            review.setRating(3);
            reviews.add(review);
        }
        
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setReviews(reviews);
        
        when(bookRepository.findByIdWithReviews(bookId)).thenReturn(Optional.of(book));
        
        // Act
        double result = bookService.getAverageRating(bookId);
        
        // Assert
        assertEquals(3.0, result, 0.01);
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }

    @Test
    void getBookByIdOrThrow_whenRepositoryThrowsException_shouldThrowDatabaseOperationException() {
        // Arrange
        when(bookRepository.findById(1L))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        assertThrows(DatabaseOperationException.class, () -> bookService.getBookByIdOrThrow(1L));
        
        // Verify repository was called
        verify(bookRepository, times(1)).findById(1L);
    }
}