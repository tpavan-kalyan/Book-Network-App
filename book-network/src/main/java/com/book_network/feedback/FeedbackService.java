package com.book_network.feedback;

import java.util.Objects;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.book_network.book.Book;
import com.book_network.book.BookRepository;
import com.book_network.common.PageResponse;
import com.book_network.exception.OperationNotPermittedException;
import com.book_network.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackService {
	
	private final BookRepository bookRepository;
	private final FeedbackMapper feedbackMapper;
	private final FeedbackRepsitory feedbackRepsitory;
	
	public Integer save(FeedbackRequest reqest, Authentication connectedUser) {
		
		Book book = bookRepository.findById(reqest.bookId())
						.orElseThrow(() -> new EntityNotFoundException("No book found with the ID ::"+reqest.bookId()));
		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException("You cannot give a feedback for an archived or not sharable book");
		}
		
		User user = ((User) connectedUser.getPrincipal());
		if (Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot give a feedback to your own book");		
			
		}
		Feedback feedback = feedbackMapper.toFeedback(reqest);
		
		return feedbackRepsitory.save(feedback).getId();
	}
	
	public PageResponse<FeedbackResponse> finadAllFeedbackByBook(Integer bookId, int page, int size, Authentication connectedUser) {
		
		Pageable pageable = PageRequest.of(page, size);
		User user = ((User) connectedUser.getPrincipal());
		Page<Feedback> feedbacks = feedbackRepsitory.findAllByBookId(bookId, pageable);
		List<FeedbackResponse> feedbackResponses = feedbacks.stream()
									.map(f -> feedbackMapper.toFeedbackResponse(f, user.getId())).toList();
		return new PageResponse<>(
					feedbackResponses,
					feedbacks.getNumber(),
					feedbacks.getSize(),
					feedbacks.getTotalElements(),
					feedbacks.getTotalPages(),
					feedbacks.isFirst(),
					feedbacks.isLast()
				);
	}
	
	

}
