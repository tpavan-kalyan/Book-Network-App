package com.book_network.book;

import static com.book_network.book.BookSpecification.withOwnerId;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.book_network.common.PageResponse;
import com.book_network.exception.OperationNotPermittedException;
import com.book_network.file.FileStorageService;
import com.book_network.history.BookTransactionHistory;
import com.book_network.history.BookTransactionHistoryRepository;
import com.book_network.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
	
	private final BookRepository bookRepository;
	private final BookMapper bookMapper;
	private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
	private final FileStorageService fileStorageService;
	
	
	public Integer save(BookRequest request, Authentication connectedUser) {
		
		User user = ((User) connectedUser.getPrincipal());
		Book book = bookMapper.toBook(request);
		book.setOwner(user);
		return bookRepository.save(book).getId();
	}
	
	public BookResponse findById(Integer bookId) {
		return bookRepository.findById(bookId)
				.map(bookMapper::toBookResponse)
				.orElseThrow(() -> new EntityNotFoundException("Noo bokk found with the Id:: "+bookId ));
	}
	
	public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAllDiaplayableBooks(pageable, user.getId());
		
		List<BookResponse> bookResponse = books.stream()
				.map(bookMapper::toBookResponse)
				.toList();
		
		return new PageResponse<>(
				bookResponse,
				books. getNumber(),
				books.getSize(),
				books. getTotalElements(),
				books.getTotalPages(),
				books. isFirst(),
				books. isLast()
				);
	}

	public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
		
		List<BookResponse> bookResponse = books.stream()
				.map(bookMapper::toBookResponse)
				.toList();
		
		return new PageResponse<>(
				bookResponse,
				books.getNumber(),
				books.getSize(),
				books.getTotalElements(),
				books.getTotalPages(),
				books.isFirst(),
				books.isLast()
				);
	}

	public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
		
		List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
														.map(bookMapper::toBorrowedBookResponse)
														.toList();
		return new PageResponse<>(
				bookResponses,
				allBorrowedBooks. getNumber(),
				allBorrowedBooks.getSize(),
				allBorrowedBooks. getTotalElements(),
				allBorrowedBooks.getTotalPages(),
				allBorrowedBooks. isFirst(),
				allBorrowedBooks. isLast()
				);
	}

	
	public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
		
		List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
														.map(bookMapper::toBorrowedBookResponse)
														.toList();
		return new PageResponse<>(
				bookResponses,
				allBorrowedBooks. getNumber(),
				allBorrowedBooks.getSize(),
				allBorrowedBooks. getTotalElements(),
				allBorrowedBooks.getTotalPages(),
				allBorrowedBooks. isFirst(),
				allBorrowedBooks. isLast()
				);
	}

	public Integer updataSharableStatus(Integer bookId, Authentication connectedUser) {
		Book book = bookRepository.findById(bookId)
							.orElseThrow(() -> new EntityNotFoundException("No book found withh the id :: " + bookId));
		User user = ((User) connectedUser.getPrincipal());
		
		if (!Objects.equals(book.getOwner().getId(), user.getId())) {
			//Throw an Exception
			throw new OperationNotPermittedException("You can not update others books sharable status");
		}
		
		book.setShareable(!book.isShareable());
		bookRepository.save(book);
		
		return bookId;
	}

	public Integer updataArchivedStatus(Integer bookId, Authentication connectedUser) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found withh the id :: " + bookId));
		User user = ((User) connectedUser.getPrincipal());

		if (!Objects.equals(book.getOwner().getId(), user.getId())) {
//Throw an Exception
			throw new OperationNotPermittedException("You can not update others books archived status");
		}

		book.setShareable(!book.isArchived());
		bookRepository.save(book);

		return bookId;
	}

	public Integer borrowBook(Integer bookId, Authentication connectedUser) {
		
		Book book = bookRepository.findById(bookId)
							.orElseThrow(() -> new EntityNotFoundException("No book found with ID::  " + bookId));
		
		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not sharable");
		}
		User user = ((User) connectedUser.getPrincipal());
		if (Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot borrow your own book");
		}
		
		final boolean isAlreadyBorrowd = bookTransactionHistoryRepository.isAlreadyBorrowdByUser(bookId, user.getId());
		if (isAlreadyBorrowd) {
			throw new OperationNotPermittedException(" The requested book is already borrowed");
		}
		
		BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
																		.user(user)
																		.book(book)
																		.returned(false)
																		.returnApproved(false)
																		.build();
		
		return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
	}

	public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
		
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with ID::  " + bookId));
		
		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not sharable");
		}
		
		User user = ((User) connectedUser.getPrincipal());
		if (Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot borrow o return your own book");
		}
		
		BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository
																.findByBookIdAndUserId(bookId, user.getId())
																.orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));
		bookTransactionHistory.setReturned(true);
		return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
	}

	public Integer approvedReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
		
		Book book = bookRepository.findById(bookId)
		.orElseThrow(() -> new EntityNotFoundException("No book found with ID::  " + bookId));
		
		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not sharable");
		}
		
		User user = ((User) connectedUser.getPrincipal());
		if (Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot borrow o return your own book");
		}
		
		BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository
				.findByBookIdAndOwnerId(bookId, user.getId())
				.orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return "));

		bookTransactionHistory.setReturnApproved(true);
		  
		return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
	}

	public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
		
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with ID::  " + bookId));
		User user = ((User) connectedUser.getPrincipal());
		
		var bookCover = fileStorageService.saveFile(file, user.getId());
		book.setBookCover(bookCover);
		bookRepository.save(book);

	}

	public Integer removeBook(Integer bookId, Authentication connectedUser) {
		
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with ID::  " + bookId));
		User user = ((User) connectedUser.getPrincipal());
		
		if (!Objects.equals(book.getOwner().getId(), user.getId())) {
				throw new OperationNotPermittedException("You can not delete others books ");
		}
		bookRepository.delete(book);
		return bookId;
	}	
	
	
	
}


















