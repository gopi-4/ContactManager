package com.smart.service;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.enums.Role;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class SearchService {

	private final Logger logger = LogManager.getLogger(SearchService.class);
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private UserRepository userRepository;

	public String searchContact(Integer page, String contactName, HttpSession session, Model model){

		try {
			User user = (User) session.getAttribute("session_user");

			Pageable pageable = PageRequest.of(page, 5);
			Page<Contact> contacts = this.contactRepository.findByNameContainingAndUserId(contactName, user.getId(), pageable);

			if (page <= contacts.getTotalPages() && page >= 0) {
				model.addAttribute("contacts", contacts);
				model.addAttribute("currentPage", page);
				model.addAttribute("totalPages", contacts.getTotalPages() - 1);
				model.addAttribute("title", "View Contacts");
				model.addAttribute("user", user);
			} else {
				model.addAttribute("title", "Error");
			}
		}catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "user/viewContacts";
	}

	public String searchUser(Integer page, String contactName, HttpSession session, Model model){
		
		try {

			User admin = (User) session.getAttribute("session_user");

			Pageable pageable = PageRequest.of(page, 5);
			Page<User> users = this.userRepository.findByNameContainingAndRole(contactName, Role.ROLE_USER, pageable);

			if (page <= users.getTotalPages() && page >= 0) {
				model.addAttribute("users", users);
				model.addAttribute("currentPage", page);
				model.addAttribute("totalPages", users.getTotalPages() - 1);
				model.addAttribute("title", "View Users");
				model.addAttribute("admin", admin);
			} else {
				model.addAttribute("title", "Error");
			}
		}catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "admin/viewUsers";
	}
}
