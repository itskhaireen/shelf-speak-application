package com.example.bookreviewapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Data transfer object for creating a new book")
public class CreateBookDTO {

    @Schema(description = "Title of the book", example = "The Great Gatsby", required = true)
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Schema(description = "Author of the book", example = "F. Scott Fitzgerald", required = true)
    @NotBlank(message = "Author is required")
    @Size(max = 100, message = "Author must not exceed 100 characters")
    private String author;

    @Schema(description = "Genre of the book", example = "Fiction", required = true)
    @NotBlank(message = "Genre is required")
    @Size(max = 50, message = "Genre must not exceed 100 characters")
    private String genre;
    
}