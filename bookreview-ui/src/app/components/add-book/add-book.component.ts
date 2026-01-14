import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookService, CreateBook } from '../../services/book.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-add-book',
  standalone: true,
  imports: [ CommonModule, FormsModule ],
  templateUrl: './add-book.component.html',
  styleUrl: './add-book.component.css'
})

export class AddBookComponent {
  book: CreateBook = {
    title: '',
    author: '',
    genre: ''
  };

  successMessage : string | null = null;
  errorMessage : string | null = null;
  isLoggedIn = false; 

  constructor(
    private bookService: BookService,
    private authService: AuthService
   ) 
   
  { this.isLoggedIn = this.authService.isAuthenticated(); }

  onSubmit() {
    this.successMessage = null;
    this.errorMessage = null;

    this.bookService.createBook(this.book).subscribe({
      next: (createdBook) => {
        this.successMessage = 'Book "${createdBook.title}" added successfully!';
        this.book = {title: '', author: '', genre: '' };

        // Emit event or call parent to refresh list
        window.location.reload(); // Simple refresh for now
      },
      error: (err) => {
        this.errorMessage = 'Failed to add book. Please try again.';
        console.error(err);
      }
    });
  }
}

// Login is fine but no add book appear?