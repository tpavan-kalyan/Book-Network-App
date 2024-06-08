package com.book_network.book;

import java.util.List;

import com.book_network.common.BaseEntity;
import com.book_network.feedback.Feedback;
import com.book_network.history.BookTransactionHistory;
import com.book_network.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {

	private String title;
	private String authorName;
	private String isbn;
	private String synopsis;
	private String bookCover;
	private boolean archived;
	private boolean shareable;

	@ManyToOne
	@JoinColumn(name = "owne_id")
	private User owner;

	@OneToMany(mappedBy = "book")
	private List<Feedback> feedBacks;

	@OneToMany(mappedBy = "book")
	private List<BookTransactionHistory> histories;

	@Transient
	public double getRate() {
		if (feedBacks == null || feedBacks.isEmpty()) {
			return 0.0;
		}

		var rate = this.feedBacks.stream()
				.mapToDouble(Feedback::getNote)
				.average()
				.orElse(0.0);
		// 3.25 -->3.0 || 3.65--> 4.0
		double roundedRate = Math.round(rate * 10.0) / 10.0;
		return roundedRate;
	}
}
