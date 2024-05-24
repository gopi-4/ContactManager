package com.smart.service;

import com.smart.dto.Message;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.repository.ContactRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class AdminService {

	private final Logger logger = LogManager.getLogger(AdminService.class);
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private ImageService imageService;
	@Autowired
	private SessionRegistry sessionRegistry;

	public String viewUsers(Integer page, Model model, HttpSession session) {

		model.addAttribute("title", "Error");
		try {

			List<Integer> online_users = sessionRegistry.getAllPrincipals().stream()
					.filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
					.map(Object::hashCode)
					.toList();

			Page<User> users = StaticServices.getUsers(page, session, online_users);

			if (page <= users.getTotalPages() && page >= 0) {
				model.addAttribute("users", users);
				model.addAttribute("currentPage", page);
				model.addAttribute("totalPages", users.getTotalPages()-1);
				model.addAttribute("title", "View Users");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "admin/viewUsers";
	}

	public String deleteUser(Integer index, HttpSession session) {
		try {

			List<User> users = (List<User>) session.getAttribute("users");
			User user = users.get(index);

			this.imageService.delete(user.getImage().getPublic_id());

			List<Contact> contacts = this.contactRepository.findContactsByUserId(user.getId());
			for(Contact contact : contacts) this.imageService.delete(contact.getImage().getPublic_id());
			this.contactRepository.deleteByUserId(user.getId());

			users.remove(user);
			session.setAttribute("users", users);

			StaticServices.getApiCall("https://contactmanager-3c3x.onrender.com/contactRegistrationStatus/"+user.getEmail()+"/false");
			session.setAttribute("message", new Message("User Deleted Successfully...", "alert-success"));
			logger.info(user.getEmail()+" Deleted.");
		} catch (Exception e) {
			logger.error(e.getMessage());
			session.setAttribute("message", new Message("Error Deleting Contact...", "alert-danger"));
		}
		return "redirect:/admin/viewUsers/0";
	}
}