import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PageResponseBookResponse } from 'src/app/services/models/page-response-book-response';
import { BookService } from 'src/app/services/services/book.service';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit{

  bookResponse: PageResponseBookResponse = {};

  page = 0;
  size = 5;

  constructor(
    private bookService: BookService,
    private router: Router
  ){}

  ngOnInit(): void {
   this.findAllBooks();
  }
  findAllBooks() {
    this.bookService.findAllBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (books) => {
        this.bookResponse = books;
      }
    });
  }
}