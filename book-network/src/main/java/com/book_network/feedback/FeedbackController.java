package com.book_network.feedback;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book_network.common.PageResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

	private final FeedbackService service;

	public ResponseEntity<Integer> saveFeedback(@Valid @RequestBody FeedbackRequest reqest,
			Authentication connectedUser) {

		return ResponseEntity.ok(service.save(reqest, connectedUser));
	}

	@GetMapping("/book/{book-id}")
	public ResponseEntity<PageResponse<FeedbackResponse>> finadAllFeedbackByBook(
			@PathVariable("book-id") Integer bookId,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size, Authentication connectedUser

	) {
		return ResponseEntity.ok(service.finadAllFeedbackByBook(bookId, page, size, connectedUser));
	}

}
