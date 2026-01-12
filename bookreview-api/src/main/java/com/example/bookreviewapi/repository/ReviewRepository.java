package com.example.bookreviewapi.repository;

import com.example.bookreviewapi.model.Review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBookId(Long bookId);
    
}

// This interface extends JpaRepository, which provides CRUD operations for the Review entity.
    // It allows you to perform operations like saving, deleting, and finding reviews without needing to implement these methods manually.
    // The Long type parameter indicates that the ID of the Review entity is of type Long.
    // You can use this repository in your service layer to interact with the database for Review entities.
    // You can also define custom query methods here if needed, such as finding reviews by reviewer name or rating.