import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Review {
  id: number;
  comment: string;
  rating: number;
  createdAt : string;
  updatedAt: string;
  userId: number;
  bookId: number;
}

export interface CreateReview {
  comment: string;
  rating: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  private apiUrl = 'http://localhost:8080/api/books';
  constructor(private http: HttpClient) {}

  getReviews(bookId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/${bookId}/reviews`);
  }

  addReviews(bookId: number, review: CreateReview): Observable<Review> {
    return this.http.post<Review>(`${this.apiUrl}/${bookId}/reviews`, review);
  }
}