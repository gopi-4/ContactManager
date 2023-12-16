package com.smart.repository;

import com.smart.entities.User;
import com.smart.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {

	@Transactional
	@Query("select u from User u where u.email = :email")
	Optional<User> getUserByEmail(@Param("email") String email);

	@Transactional
	Page<User> findByRole(Role role, Pageable pageable);

	@Transactional
	Page<User> findByNameContainingAndRole(String query, Role role, Pageable pageable);

}
