import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, LoginRequest } from '../../services/auth.service';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})

export class LoginComponent {
  credentials: LoginRequest = {
    usernameOrEmail: 'admin',
    password: 'admin123'
  };
  errorMessage: string | null = null;

  constructor(private authService: AuthService) {}

  onSubmit() {
    this.errorMessage = null;
    this.authService.login(this.credentials).subscribe({
      next: () => {
        window.location.reload();
      },
      error: (err) => {
        this.errorMessage = 'Login failed. Please check your credentials';
        console.error(err);
      }
    });
  }
}