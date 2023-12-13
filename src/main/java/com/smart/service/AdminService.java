package com.smart.service;

import com.smart.entities.User;
import com.smart.enums.Role;
import com.smart.helpers.Message;
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
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Date;

@Service
public class AdminService {

	private final Logger logger = LogManager.getLogger(AdminService.class);
	@Autowired
	private UserRepository userRepository;

	public String viewUsers(Integer page, Model model) {

		try {
			Pageable pageable = PageRequest.of(page, 10);
			Page<User> users = this.userRepository.findByRole(Role.ROLE_USER, pageable);
			if (page <= users.getTotalPages() && page >= 0) {
				model.addAttribute("users", users);
				model.addAttribute("currentPage", page);
				model.addAttribute("totalPages", users.getTotalPages()-1);
				model.addAttribute("title", "View Users");
			} else {
				model.addAttribute("title", "Error");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return "admin/viewUsers";
	}

	public String showUserDetails(Integer Id, Model model) {

		User user = this.userRepository.findById(Id).orElse(null);
		model.addAttribute("user",user);
		return "admin/userDetails";
	}

	public String deleteContact(Integer Id, HttpSession session) {

		try {
			User user = this.userRepository.findById(Id).orElseThrow();
			this.userRepository.delete(user);
			session.setAttribute("message", new Message("User Deleted Successfully...", "success"));
		} catch (Exception e) {
			logger.error(e.getMessage());
			session.setAttribute("message", new Message("Error Deleting Contact...", "danger"));
		}
		return "redirect:/admin/viewUsers/0";
	}
	
	@GetMapping("/signOut")
	public String logout(Principal principal) {

		User user = userRepository.getUserByEmail(principal.getName()).orElse(null);
		assert user != null;
		user.setStatus(false);
		user.setDate(new Date().toString());
		this.userRepository.save(user);
		logger.info("ADMIN LOGOUT.");
		return "redirect:/signOut";
	}
}
