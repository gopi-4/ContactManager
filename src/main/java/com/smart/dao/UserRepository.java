package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.smart.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	@Transactional
	@Query("select u from User u where u.email = :email")
	public User getUserByEmail(@Param("email") String email);
	
	@Transactional
	@Query("select u from User u where u.name = :name")
	public User getUserByName(@Param("name") String name);

	@Transactional
	@Query( "select u from User u where u.role = :role" )
	Page<User> findBySpecificRoles(@Param("role") String role, Pageable pageable);

	@Transactional
	public List<User> findByNameContaining(String query);
	
	@Transactional
	@Query( "select u from User u where u.Id = :userId" )
	public User getById(int userId);

}
