import { Routes } from '@angular/router';
import { BookListComponent } from './components/book-list/book-list.component';
import { AddBookComponent } from './components/add-book/add-book.component';
import { LoginComponent } from './components/login/login.component';

export const routes: Routes = [
    { path: '', component: BookListComponent },
    { path: 'add-book', component: BookListComponent},
    { path: 'login', component: LoginComponent}
];