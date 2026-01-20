import { Routes } from '@angular/router';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { BookListComponent } from './components/book-list/book-list.component';
import { AddBookComponent } from './components/add-book/add-book.component';
import { LoginComponent } from './components/login/login.component';

export const routes: Routes = [
    { path: '', component: LandingPageComponent },
    { path: 'book-list', component: BookListComponent},
    { path: 'login', component: LoginComponent},
    { path: 'add-book', component: AddBookComponent}
];