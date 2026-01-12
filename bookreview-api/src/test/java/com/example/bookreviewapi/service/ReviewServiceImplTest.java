package com.example.bookreviewapi.service;

import com.example.bookreviewapi.exception.BookNotFoundException;
import com.example.bookreviewapi.exception.InvalidReviewDataException;
import com.example.bookreviewapi.exception.DatabaseOperationException;
import com.example.bookreviewapi.model.Book;
import com.example.bookreviewapi.model.Review;
import com.example.bookreviewapi.model.User;
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
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void saveReview_whenValidReviewAndBookExists_shouldSaveSuccessfully() {
        // Arrange
        Long bookId = 1L;
        
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        
        User user = new User();
        user.setId(10L);
        user.setUsername("John Doe");
        
        Review inputReview = new Review();
        inputReview.setUser(user);
        inputReview.setComment("Great book!");
        inputReview.setRating(5);
        
        Review savedReview = new Review();
        savedReview.setId(1L);
        savedReview.setUser(user);
        savedReview.setComment("Great book!");
        savedReview.setRating(5);
        savedReview.setBook(book);
        
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        
        // Act
        Review result = reviewService.saveReview(bookId, inputReview);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getUser().getUsername());
        assertEquals("Great book!", result.getComment());
        assertEquals(5, result.getRating());
        assertEquals(book, result.getBook());
        
        // Verify the book relationship was set
        verify(bookRepository, times(1)).findById(bookId);
        verify(reviewRepository, times(1)).save(inputReview);
        
        // Verify the book was set on the review before saving
        assertEquals(book, inputReview.getBook());
    }

    @Test
    void getReviewsByBookId_whenBookExistsWithReviews_shouldReturnReviews() {
        // Arrange
        Long bookId = 1L;
        
        // Create users
        User user1 = new User();
        user1.setId(10L);
        user1.setUsername("John");
        User user2 = new User();
        user2.setId(11L);
        user2.setUsername("Jane");
        // Create reviews
        Review review1 = new Review();
        review1.setId(1L);
        review1.setRating(5);
        review1.setUser(user1);
        review1.setComment("Great book!");
        
        Review review2 = new Review();
        review2.setId(2L);
        review2.setRating(4);
        review2.setUser(user2);
        review2.setComment("Good book");
        
        // Create book with reviews
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setReviews(List.of(review1, review2));
        
        when(bookRepository.findByIdWithReviews(bookId)).thenReturn(Optional.of(book));
        
        // Act
        List<Review> result = reviewService.getReviewsByBookId(bookId);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUser().getUsername()).isEqualTo("John");
        assertThat(result.get(1).getUser().getUsername()).isEqualTo("Jane");
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }
    
    @Test
    void getReviewsByBookId_whenBookExistsButNoReviews_shouldReturnEmptyList() {
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
        List<Review> result = reviewService.getReviewsByBookId(bookId);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }
    
    @Test
    void getReviewsByBookId_whenBookExistsButReviewsIsNull_shouldReturnEmptyList() {
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
        List<Review> result = reviewService.getReviewsByBookId(bookId);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }
    
    @Test
    void getReviewsByBookId_whenBookDoesNotExist_shouldThrowBookNotFoundException() {
        // Arrange
        Long bookId = 999L;
        
        when(bookRepository.findByIdWithReviews(bookId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> reviewService.getReviewsByBookId(bookId));
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }

    // ========== EXCEPTION HANDLING TESTS ==========

    @Test
    void saveReview_whenBookDoesNotExist_shouldThrowBookNotFoundException() {
        // Arrange
        Long bookId = 1L;
        User user = new User();
        user.setUsername("testuser");
        Review review = new Review();
        review.setUser(user);
        review.setComment("Great book!");
        review.setRating(5);
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> reviewService.saveReview(bookId, review));
        // Verify repository was called
        verify(bookRepository, times(1)).findById(bookId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_whenReviewIsNull_shouldThrowInvalidReviewDataException() {
        // Arrange
        Long bookId = 1L;
        
        // Act & Assert
        assertThrows(InvalidReviewDataException.class, () -> reviewService.saveReview(bookId, null));
        
        // Verify no repository calls were made
        verify(bookRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_whenReviewerIsNull_shouldThrowInvalidReviewDataException() {
        // Arrange
        Long bookId = 1L;
        Review review = new Review();
        review.setUser(null);
        review.setComment("Great book!");
        review.setRating(5);
        
        // Act & Assert
        assertThrows(InvalidReviewDataException.class, () -> reviewService.saveReview(bookId, review));
        
        // Verify no repository calls were made
        verify(bookRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_whenReviewerIsEmpty_shouldThrowInvalidReviewDataException() {
        // Arrange
        Long bookId = 1L;
        Review review = new Review();
        review.setUser(new User()); // User with no username
        review.setComment("Great book!");
        review.setRating(5);
        // Act & Assert
        assertThrows(InvalidReviewDataException.class, () -> reviewService.saveReview(bookId, review));
        // Verify no repository calls were made
        verify(bookRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_whenCommentIsNull_shouldThrowInvalidReviewDataException() {
        // Arrange
        Long bookId = 1L;
        Review review = new Review();
        User user = new User();
        user.setUsername("testuser");
        review.setUser(user);
        review.setComment(null);
        review.setRating(5);
        
        // Act & Assert
        assertThrows(InvalidReviewDataException.class, () -> reviewService.saveReview(bookId, review));
        
        // Verify no repository calls were made
        verify(bookRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_whenCommentIsEmpty_shouldThrowInvalidReviewDataException() {
        // Arrange
        Long bookId = 1L;
        Review review = new Review();
        User user = new User();
        user.setUsername("testuser");
        review.setUser(user);
        review.setComment("");
        review.setRating(5);
        
        // Act & Assert
        assertThrows(InvalidReviewDataException.class, () -> reviewService.saveReview(bookId, review));
        
        // Verify no repository calls were made
        verify(bookRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_whenRatingIsZero_shouldThrowInvalidReviewDataException() {
        // Arrange
        Long bookId = 1L;
        Review review = new Review();
        User user = new User();
        user.setUsername("testuser");
        review.setUser(user);
        review.setComment("Great book!");
        review.setRating(0);
        
        // Act & Assert
        assertThrows(InvalidReviewDataException.class, () -> reviewService.saveReview(bookId, review));
        
        // Verify no repository calls were made
        verify(bookRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_whenRatingIsSix_shouldThrowInvalidReviewDataException() {
        // Arrange
        Long bookId = 1L;
        Review review = new Review();
        User user = new User();
        user.setUsername("testuser");
        review.setUser(user);
        review.setComment("Great book!");
        review.setRating(6);
        
        // Act & Assert
        assertThrows(InvalidReviewDataException.class, () -> reviewService.saveReview(bookId, review));
        
        // Verify no repository calls were made
        verify(bookRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_whenRatingIsNegative_shouldThrowInvalidReviewDataException() {
        // Arrange
        Long bookId = 1L;
        Review review = new Review();
        User user = new User();
        user.setUsername("testuser");
        review.setUser(user);
        review.setComment("Great book!");
        review.setRating(-1);
        
        // Act & Assert
        assertThrows(InvalidReviewDataException.class, () -> reviewService.saveReview(bookId, review));
        
        // Verify no repository calls were made
        verify(bookRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    // ========== DATABASE EXCEPTION TESTS ==========

    @Test
    void saveReview_whenBookRepositoryThrowsException_shouldThrowDatabaseOperationException() {
        // Arrange
        Long bookId = 1L;
        Review review = new Review();
        User user = new User();
        user.setUsername("testuser");
        review.setUser(user);
        review.setComment("Great book!");
        review.setRating(5);
        
        when(bookRepository.findById(bookId))
            .thenThrow(new RuntimeException("Database connection failed"));
        
        // Act & Assert
        assertThrows(DatabaseOperationException.class, () -> reviewService.saveReview(bookId, review));
        
        // Verify repository was called
        verify(bookRepository, times(1)).findById(bookId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_whenReviewRepositoryThrowsException_shouldThrowDatabaseOperationException() {
        // Arrange
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        User user = new User();
        user.setUsername("testuser");
        Review review = new Review();
        review.setUser(user);
        review.setComment("Great book!");
        review.setRating(5);
        
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(reviewRepository.save(any(Review.class)))
            .thenThrow(new RuntimeException("Save operation failed"));
        
        // Act & Assert
        assertThrows(DatabaseOperationException.class, () -> reviewService.saveReview(bookId, review));
        
        // Verify both repositories were called
        verify(bookRepository, times(1)).findById(bookId);
        verify(reviewRepository, times(1)).save(any());
    }

    @Test
    void getReviewsByBookId_whenBookRepositoryThrowsException_shouldThrowDatabaseOperationException() {
        // Arrange
        Long bookId = 1L;
        
        when(bookRepository.findByIdWithReviews(bookId)).thenThrow(new RuntimeException("Database error"));
        
        // Act & Assert
        assertThrows(DatabaseOperationException.class, () -> reviewService.getReviewsByBookId(bookId));
        
        // Verify repository was called
        verify(bookRepository, times(1)).findByIdWithReviews(bookId);
    }

    // ========== BOUNDARY VALUE TESTS ==========

    @Test
    void saveReview_whenRatingIsOne_shouldSaveSuccessfully() {
        // Arrange
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        User user = new User();
        user.setUsername("testuser");
        Review inputReview = new Review();
        inputReview.setUser(user);
        inputReview.setComment("Great book!");
        inputReview.setRating(1); // Minimum rating
        Review savedReview = new Review();
        savedReview.setId(1L);
        savedReview.setUser(user);
        savedReview.setComment("Great book!");
        savedReview.setRating(1);
        savedReview.setBook(book);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        // Act
        Review result = reviewService.saveReview(bookId, inputReview);
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUser().getUsername());
        assertEquals("Great book!", result.getComment());
        assertEquals(1, result.getRating());
        assertEquals(book, result.getBook());
        // Verify the book relationship was set
        verify(bookRepository, times(1)).findById(bookId);
        verify(reviewRepository, times(1)).save(inputReview);
        // Verify the book was set on the review before saving
        assertEquals(book, inputReview.getBook());
    }

    @Test
    void saveReview_whenRatingIsFive_shouldSaveSuccessfully() {
        // Arrange
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        User user = new User();
        user.setUsername("testuser");
        Review inputReview = new Review();
        inputReview.setUser(user);
        inputReview.setComment("Great book!");
        inputReview.setRating(5); // Maximum rating
        Review savedReview = new Review();
        savedReview.setId(1L);
        savedReview.setUser(user);
        savedReview.setComment("Great book!");
        savedReview.setRating(5);
        savedReview.setBook(book);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        // Act
        Review result = reviewService.saveReview(bookId, inputReview);
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUser().getUsername());
        assertEquals("Great book!", result.getComment());
        assertEquals(5, result.getRating());
        assertEquals(book, result.getBook());
        // Verify the book relationship was set
        verify(bookRepository, times(1)).findById(bookId);
        verify(reviewRepository, times(1)).save(inputReview);
        // Verify the book was set on the review before saving
        assertEquals(book, inputReview.getBook());
    }
} 