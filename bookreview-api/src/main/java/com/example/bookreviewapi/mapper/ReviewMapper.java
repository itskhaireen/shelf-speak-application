package com.example.bookreviewapi.mapper;

import com.example.bookreviewapi.dto.CreateReviewDTO;
import com.example.bookreviewapi.dto.ReviewDTO;
import com.example.bookreviewapi.model.Review;

public class ReviewMapper {

    // call a static method directly using the class name, without needing to create an instance of the class.
    
    // Mapped CreateReviewDTO --> Review (Input - POST API)
    // No IDs because it's auto generated in the Review.java (Entity)
    public static Review toEntity(CreateReviewDTO dto) {
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        // Note: user will be set by the service layer after authentication

        return review;
    }

    // Mapped Review --> ReviewDTO (Output - GET API)
    public static ReviewDTO toDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        
        // Get reviewer information from the user relationship
        if (review.getUser() != null) {
            dto.setReviewerName(review.getUser().getUsername());
            dto.setUserId(review.getUser().getId());
        } else {
            dto.setReviewerName("Unknown");
            dto.setUserId(null);
        }
        
        // Format dates
        if (review.getCreatedAt() != null) {
            dto.setCreatedAt(review.getCreatedAt().toString());
        }
        if (review.getUpdatedAt() != null) {
            dto.setUpdatedAt(review.getUpdatedAt().toString());
        }

        return dto;
    }
    
}