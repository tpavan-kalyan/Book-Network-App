package com.book_network.role;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.book_network.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role")
public class Role 
{
	@Id
	@GeneratedValue
	private Integer id;
	@Column(unique = true)
	private String name;
	
	@ManyToMany(mappedBy = "roles")
	@JsonIgnore
	private List<User> users;
	
	
	@CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;
    
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime modifiedDate;
	
}
