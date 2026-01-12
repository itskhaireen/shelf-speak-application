import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Book {
  id: number;
  title: string;
  author: string;
  genre: string;
}

export interface CreateBook {
  title : string;
  author : string;
  genre: string;
}

@Injectable({
  providedIn: 'root'
})
export class BookService {

  private apiUrl = 'http://localhost:8080/api/books';

  constructor(private http: HttpClient) { }

  getAllBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(this.apiUrl);
  }

  createBook(book: CreateBook): Observable<Book> {
    return this.http.post<Book>(this.apiUrl, book);
  }
}