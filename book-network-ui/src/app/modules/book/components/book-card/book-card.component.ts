import { Component, EventEmitter, Input, Output } from '@angular/core';
import { BookResponse } from 'src/app/services/models';

@Component({
  selector: 'app-book-card',
  templateUrl: './book-card.component.html',
  styleUrls: ['./book-card.component.scss']
})
export class BookCardComponent {

  @Output() private share: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private archvie: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private addToWaitingList: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private borrow: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private edit: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private details: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();

  private _book: BookResponse = {};
  private _bookCover: string | undefined;
  private _manage: boolean = false;

  onArchive() {
    this.archvie.emit(this._book)
  }
  onShare() {
    this.share.emit(this._book)
  }
  onEdit() {
    this.edit.emit(this._book)
  }
  onAddToWaitingList() {
    this.addToWaitingList.emit(this._book)
  }
  onBorrow() {
    this.borrow.emit(this._book)
  }
  onShowDetails() {
    this.details.emit(this._book)
  }


  public get manage(): boolean {
    return this._manage;
  }

  @Input()
  public set manage(value: boolean) {
    this._manage = value;
  }

  public get bookCover(): string | undefined {
    if (this._book.cover) {
      return 'data:image/jpg;base64, ' + this._book.cover;
    }

    return 'https://picsum.photos/200/300';
  }

  public get book(): BookResponse {
    return this._book;
  }
  @Input()
  public set book(value: BookResponse) {
    this._book = value;
  }

}
