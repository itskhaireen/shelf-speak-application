package com.example.bookreviewapi.service;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import com.example.bookreviewapi.repository.BookRepository;
import com.example.bookreviewapi.repository.ReviewRepository;
import com.example.bookreviewapi.model.Review;
import com.example.bookreviewapi.exception.BookNotFoundException;
import com.example.bookreviewapi.exception.InvalidReviewDataException;
import com.example.bookreviewapi.exception.DatabaseOperationException;
import com.example.bookreviewapi.model.Book;

@Service
public class ReviewServiceImpl implements ReviewService {
    
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(BookRepository bookRepository, ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review saveReview(Long bookId, Review review) {
        try {
            // Validate review data
            validateReviewData(review);
            
            // Find the book and validate it exists
            Book book = getBookByIdOrThrow(bookId);
            
            // Set the book relationship
            review.setBook(book);
            
            // Manage bidirectional relationship
            if (book.getReviews() == null) {
                book.setReviews(new java.util.ArrayList<>());
            }
            book.getReviews().add(review);
            
            // Set timestamps
            if (review.getCreatedAt() == null) {
                review.setCreatedAt(LocalDateTime.now());
            }
            review.setUpdatedAt(LocalDateTime.now());
            
            // Save the review
            return reviewRepository.save(review);
            
        } catch (BookNotFoundException | InvalidReviewDataException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            throw new DatabaseOperationException("save review", e);
        }
    }

    @Override
    public List<Review> getReviewsByBookId(Long bookId) {
        try {
            // Find the book with reviews loaded and validate it exists
            Book book = bookRepository.findByIdWithReviews(bookId)
                    .orElseThrow(() -> new BookNotFoundException(bookId));
            
            // Return the reviews from the book, ensuring we never return null
            List<Review> reviews = book.getReviews();
            return reviews != null ? reviews : new java.util.ArrayList<>();
            
        } catch (BookNotFoundException e) {
            throw e; // Re-throw business exception
        } catch (Exception e) {
            throw new DatabaseOperationException("get reviews by book id", e);
        }
    }

    @Override
    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
            .orElseThrow(() -> new com.example.bookreviewapi.exception.InvalidReviewDataException("Review not found with id: " + reviewId));
    }

    @Override
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    /**
     * Helper method to get book by ID with proper exception handling
     */
    private Book getBookByIdOrThrow(Long bookId) {
        return bookRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    /**
     * Validate review data before saving
     * Note: Basic format validation is handled by CreateReviewDTO @Valid annotations
     * This method focuses on business logic validation
     */
    private void validateReviewData(Review review) {
        if (review == null) {
            throw new InvalidReviewDataException("Review cannot be null");
        }
        
        // Business logic validations (beyond basic format validation)
        
        // For now, keep basic null checks as a safety net
        if (review.getUser() == null) {
            throw new InvalidReviewDataException("Review must be associated with a user");
        }
        
        // New: Check for empty username
        if (review.getUser().getUsername() == null || review.getUser().getUsername().trim().isEmpty()) {
            throw new InvalidReviewDataException("Reviewer username cannot be null or empty");
        }
        
        if (review.getComment() == null || review.getComment().trim().isEmpty()) {
            throw new InvalidReviewDataException("Review comment cannot be null or empty");
        }
        
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new InvalidReviewDataException("Rating must be between 1 and 5");
        }
    }
}