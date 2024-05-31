package com.book_network.book;

import java.util.List;

import com.book_network.common.BaseEntity;
import com.book_network.feedback.FeedBack;
import com.book_network.history.BookTransactionHistory;
import com.book_network.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class Book extends BaseEntity{
	
	@Id
	@GeneratedValue
	private Integer id;
	private String title;
	private String authorName;
	private String isbn;
	private String synopsis;
	private boolean archived;
	private boolean shareable;
	
	@ManyToOne
	@JoinColumn(name = "owne_id")
	private User owner;
	
	 @OneToMany(mappedBy = "book")
	 private List<FeedBack> feedBacks;
	 
	 @OneToMany(mappedBy = "book")
	 private List<BookTransactionHistory> histories;
}
