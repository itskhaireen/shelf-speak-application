import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';

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

}