package com.example.bookreviewapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Data transfer object for creating a new review")
public class CreateReviewDTO {

    @Schema(description = "Review comment", example = "This is an excellent book with great storytelling!", required = true)
    @NotBlank(message = "Comment is required")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;

    @Schema(description = "Rating from 1 to 5", example = "5", minimum = "1", maximum = "5", required = true)
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be no more than 5")
    private int rating;


    
}