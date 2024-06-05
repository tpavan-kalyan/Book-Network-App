package com.book_network.feedback;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.book_network.book.Book;

@Service
public class FeedbackMapper {

	public Feedback toFeedback(FeedbackRequest reqest) {
		 
		return Feedback.builder()
						.note(reqest.note())
						.comment(reqest.comment())
						.book(Book.builder()
								.id(reqest.bookId())
								.archived(false)
								.shareable(false)
								.build())
						.build();
		
	}

	public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
		// TODO Auto-generated method stub
		return FeedbackResponse.builder()
						.note(feedback.getNote())
						.comment(feedback.getComment())
						.ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
						.build();
	}

}
