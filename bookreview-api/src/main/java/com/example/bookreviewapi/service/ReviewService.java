package com.example.bookreviewapi.service;

import com.example.bookreviewapi.model.Review;

import java.util.List;

public interface ReviewService {
    
    Review saveReview(Long BookId, Review review);
    List<Review> getReviewsByBookId(Long bookId);
    Review getReviewById(Long reviewId);
    void deleteReview(Long reviewId);
}
