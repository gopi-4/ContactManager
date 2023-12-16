package com.smart.service;

import com.smart.dto.Message;
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

import java.util.List;

@Service
public class AdminService {

	private final Logger logger = LogManager.getLogger(AdminService.class);
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private ImageService imageService;

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
			model.addAttribute("title", "Error");
			logger.error(e.getMessage());
		}
		return "admin/viewUsers";
	}

	public String deleteUser(Integer Id, HttpSession session) {
		try {
			User user = this.userRepository.findById(Id).orElse(null);
			assert user != null;
			this.imageService.delete(user.getImage().getPublic_id());
			List<Contact> contacts = this.contactRepository.findContactsByUserId(Id);
			for(Contact contact : contacts) this.imageService.delete(contact.getImage().getPublic_id());
			this.contactRepository.deleteByUserId(Id);
			this.userRepository.deleteById(Id);
			session.setAttribute("message", new Message("User Deleted Successfully...", "success"));
		} catch (Exception e) {
			logger.error(e.getMessage());
			session.setAttribute("message", new Message("Error Deleting Contact...", "danger"));
		}
		return "redirect:/admin/viewUsers/0";
	}
}
