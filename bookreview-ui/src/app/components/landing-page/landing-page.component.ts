import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [ CommonModule, RouterModule],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.css'
})
export class LandingPageComponent {

  isLoggedIn = false;

  constructor(private authService: AuthService){
    this.isLoggedIn = this.authService.isAuthenticated();
  }

  logout() {
    this.authService.logout();
    window.location.href = '/';
  }

  featuredBooks = [
    {
      title: "The Witch Craft",
      author: "Putri K. Jasmin",
      genre: "Horror",
      cover: "/images/witchcraft_book_cover.jpg"
    },
    {
      title: "The Casualty",
      author: "Daniel Kang",
      genre: "Fantasy",
      cover: "/images/casualty_book_cover.jpg",
    },
    {
      title: "The Designer",
      author: "Nur Farah",
      genre: "Education",
      cover: "/images/design_book_cover.jpg"
    },
    {
      title: "Horror Show",
      author: "Stephanie Young",
      genre: "Thriller",
      cover: "/images/horrorshow_book_cover.png"
    },
    {
      title: "Business Mindset",
      author: "Mateen Lutfi",
      genre: "Non-Fiction",
      cover: "/images/own_business_book_cover.avif"
    }
  ];
}