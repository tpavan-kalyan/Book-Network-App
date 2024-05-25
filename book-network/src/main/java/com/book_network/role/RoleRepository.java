package com.book_network.role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer>{
	
	Optional<Role> findBy(String role);

}
