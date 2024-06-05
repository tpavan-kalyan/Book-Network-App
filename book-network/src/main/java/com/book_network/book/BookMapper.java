package com.book_network.book;

import org.springframework.stereotype.Service;

import com.book_network.file.FileUtils;
import com.book_network.history.BookTransactionHistory;

@Service
public class BookMapper {

	public Book toBook(BookRequest request) {
		// TODO Auto-generated method stub
		return Book.builder()
				.id(request.id())
				.title(request.title())
				.authorName(request.authorName())
				.synopsis(request.synopsis())
				.archived(false)
				.shareable(request.shareable())
				.build();
	}
	
	public BookResponse toBookResponse(Book book) {
		return BookResponse
				.builder()
				.id(book.getId())
				.title(book.getTitle())
				.authorName(book.getAuthorName())
				.isbn(book.getIsbn())
				.synopsis(book.getSynopsis())
				.rate(book.getRate())
				.archived(book.isArchived())
				.shareable(book.isShareable())
				.owner(book.getOwner().getFullName())
				.cover(FileUtils.readFileFromLocation(book.getBookCover()))
				.build();
	}
	
	public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
		return BorrowedBookResponse
					.builder()
					.id(history.getBook().getId())
					.title(history.getBook().getTitle())
					.authorName(history.getBook().getAuthorName())
					.isbn(history.getBook().getIsbn())
					.rate(history.getBook().getRate())
					.returned(history.isReturned())
					.returnApproved(history.isReturnApproved())
					.build();
	}
	
}











