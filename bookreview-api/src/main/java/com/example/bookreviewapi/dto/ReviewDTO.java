package com.example.bookreviewapi.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    
    private Long id;
    private String comment;
    private int rating;
    private String reviewerName; // Will be populated from User.username
    private Long userId; // For authorization checks
    private String createdAt; // Formatted date
    private String updatedAt; // Formatted date

}