package com.smart.repository;

import com.smart.entities.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	@Transactional
	@Query("from Contact as c where c.userId =:userId")
	Page<Contact> findContactsByUserId(@Param("userId") int userId, Pageable pageable);
	@Transactional
	@Query("from Contact as c where c.userId =:userId")
	List<Contact> findContactsByUserId(@Param("userId") int userId);
	
	@Transactional
	Page<Contact> findByNameContainingAndUserId(String name, Integer userId, Pageable pageable);
	
	@Transactional
	@Query("select u from Contact u where u.email = :email")
	List<Contact> getContactByEmail(@Param("email") String email);

	@Transactional
	void deleteByUserId(int userId);
}
