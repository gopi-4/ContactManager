package com.smart.controllers;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
public class SearchController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository; 
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal, HttpSession session){
	
//		User user = this.userRepository.getUserByEmail(principal.getName());
		User user = (User) session.getAttribute("user");
		/* System.out.println(principal.getName()); */
		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
		/*
		 * for(int i=0;i<contacts.size();i++) {
		 * System.out.println(contacts.get(i).getName()); }
		 */
		
		return ResponseEntity.ok(contacts);
	}
	
	@GetMapping("/searchUser/{query}")
	public ResponseEntity<?> searchUser(@PathVariable("query") String query, Principal principal){
		
		List<User> users = this.userRepository.findByNameContaining(query);
		/* System.out.println(principal.getName()); */
//		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
		/*
		 * for(int i=0;i<contacts.size();i++) {
		 * System.out.println(contacts.get(i).getName()); }
		 */
		
		return ResponseEntity.ok(users);
	}
}
