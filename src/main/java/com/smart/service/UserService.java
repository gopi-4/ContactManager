package com.smart.service;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helpers.Message;
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
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Date;
import java.util.Objects;

@Service
public class UserService {

	private final Logger logger = LogManager.getLogger(UserService.class);
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private ImageService imageService;

	public User getUserByEmail(String email) {
		return this.userRepository.getUserByEmail(email).orElse(null);
	}

	public String updateUserHandler(User newUser, MultipartFile file, HttpSession session) {
		try {

			User user = this.userRepository.findById(newUser.getId()).orElse(null);

			if(!file.isEmpty()) {
				newUser.setImage(imageService.upload(file));
			}else {
				newUser.setImage(Objects.requireNonNull(user).getImage());
			}

			newUser.setPassword(Objects.requireNonNull(user).getPassword());
			this.userRepository.save(newUser);

			logger.info("Profile Updated.");
			session.setAttribute("message", new Message("User updated successfully...", "success"));
		} catch (Exception e) {
			logger.error(e.getMessage());
			session.setAttribute("message", new Message(e.getMessage(), "danger"));
		}
		return "redirect:/user/profile";
	}

	public String addContact(Contact contact, MultipartFile file, Model model, Principal principal, HttpSession session) {
		try {

			String emailString = principal.getName();
			User user = userRepository.getUserByEmail(emailString).orElse(null);

			if(file.isEmpty()) {
				contact.setImage(imageService.getDefault());
			}else {
				contact.setImage(imageService.upload(file));
			}

			contact.setRegister(false);
			if(this.userRepository.getUserByEmail(contact.getEmail()).orElse(null)!=null) contact.setRegister(true);

			assert user != null;
			contact.setUserId(user.getId());
			this.contactRepository.save(contact);

			logger.info("Contact Saved.");
			session.setAttribute("message", new Message("Contact Added Successfully..", "success"));
		} catch (Exception e) {
			logger.error(e.getMessage());
			session.setAttribute("message", new Message("Something Went Wrong..", "danger"));
		}
		model.addAttribute("title", "Add Contact");
		return "user/addContact";
	}

	public String viewContacts(Integer page, Model model, Principal principal) {
		try {
			String userEmail = principal.getName();
			User user = userRepository.getUserByEmail(userEmail).orElse(null);

			Pageable pageable = PageRequest.of(page, 5);

			assert user != null;
			Page<Contact> contacts = this.contactRepository.findContactsByUserId(user.getId(), pageable);

			if (page <= contacts.getTotalPages() && page >= 0) {
				model.addAttribute("contacts", contacts);
				model.addAttribute("currentPage", page);
				model.addAttribute("totalPages", contacts.getTotalPages() - 1);
				model.addAttribute("title", "View Contacts");
			} else {
				model.addAttribute("title", "Error");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "user/viewContacts";
	}

	public String updateContact(Integer Id, Model model, Integer page) {

		Contact contact = this.contactRepository.findById(Id).orElse(null);

		model.addAttribute("currentPage", page);
		model.addAttribute("contact", contact);
		model.addAttribute("title", "Update Contact");

		return "user/updateContact";
	}

	public String updateContactHandler(Contact newContact, MultipartFile file, Principal principal, HttpSession session, Integer page) {

		try {
			if(!file.isEmpty()) {
				newContact.setImage(null);
				newContact.setImage(imageService.upload(file));
			}else {
				newContact.setImage(Objects.requireNonNull(this.contactRepository.findById(newContact.getId()).orElse(null)).getImage());
			}

			User user = userRepository.getUserByEmail(principal.getName()).orElse(null);

			assert user != null;
			newContact.setUserId(user.getId());

			newContact.setRegister(false);
			if (this.userRepository.getUserByEmail(newContact.getEmail()).orElse(null)!=null) newContact.setRegister(true);

			this.contactRepository.save(newContact);

			logger.info("Contact Updated.");
			session.setAttribute("message", new Message("Your Contact updated successfully...", "success"));
		} catch (Exception e) {
			session.setAttribute("message", new Message("Error updating Contact...", "danger"));
			logger.error(e.getMessage());
		}
		return "redirect:/user/viewContacts/" + page;
	}

	public String deleteContact(Integer Id, HttpSession session) {

		try {

			this.contactRepository.deleteById(Id);
			session.setAttribute("message", new Message("Contact Deleted Successfully...", "success"));

		} catch (Exception e) {
			logger.info(e.getMessage());
			session.setAttribute("message", new Message("Error Deleting Contact...", "danger"));
		}
		return "redirect:/user/viewContacts/0";
	}

	public String logout(Principal principal) {

		User user = userRepository.getUserByEmail(principal.getName()).orElse(null);

		assert user != null;
		user.setStatus(false);
		user.setDate(new Date().toString());
		this.userRepository.save(user);
		logger.info("USER LOGOUT.");
		return "redirect:/signOut";
	}

	public String passwordUpdate(String username, Integer contactId, Integer page, HttpSession session, Model model) {
		model.addAttribute("title", "Change Password");
		try {
			Contact contact = this.contactRepository.findById(contactId).orElse(null);
			if(contact==null) {
				session.setAttribute("message", new Message("Contact not Exist..", "danger"));
				model.addAttribute("title", "View Contacts");
			}else {
				String subject = "Invite : Contact Manager";
				String message = username+" invites you to : https://contactmanager-3c3x.onrender.com/";
				String to = contact.getEmail();
				boolean flag = this.emailService.sendEmail(subject, message, to);
				if (flag) {
					session.setAttribute("message", new Message("Invite Send to "+contact.getName(), "success"));
					model.addAttribute("title", "View Contacts");
				}else {
					session.setAttribute("message", new Message("Contact Email Not Exists..", "danger"));
					model.addAttribute("title", "View Contacts");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			session.setAttribute("message", new Message("Error Sending Invite..", "danger"));
		}		
		return "redirect:/user/viewContacts/"+page;
	}

}
