package com.smart.service;

import com.smart.dto.Message;
import com.smart.entities.Contact;
import com.smart.entities.User;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

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

	RestTemplate restTemplate = new RestTemplate();

	public User getUser(int userId) {return restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/getUser/"+userId, User.class).getBody();}

	public String updateUserHandler(String username, String about, MultipartFile file, HttpSession session) {
		try {
			Integer userId = (Integer) session.getAttribute("session_user_Id");
			User user = getUser(userId);
			assert user != null;
			if(!file.isEmpty()) {
				this.imageService.delete(user.getImage().public_id);
				user.setImage(imageService.upload(file));
			}
			if (username!=null) user.setName(username);
			if (about!=null) user.setAbout(about);

			restTemplate.postForEntity("https://contactmanager-3c3x.onrender.com/updateUser", user, User.class);

			logger.info("Profile Updated.");
			session.setAttribute("message", new Message("User updated successfully...", "success"));
		} catch (Exception e) {
			logger.error(e.getMessage());
			session.setAttribute("message", new Message(e.getMessage(), "danger"));
		}
		return "redirect:/user/profile";
	}

	public String addContact(Contact contact, MultipartFile file, Model model, HttpSession session) {
		try {
			Integer userId = (Integer) session.getAttribute("session_user_Id");
			User user = getUser(userId);
			if(file.isEmpty()) {
				contact.setImage(imageService.getDefault());
			}else {
				contact.setImage(imageService.upload(file));
			}

			contact.setRegister(false);
			boolean temp = this.userRepository.getUserByEmail(contact.getEmail()).isPresent();
			if(temp) contact.setRegister(true);

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

	public String viewContacts(Integer page, Model model, HttpSession session) {
		try {

			Integer userId = (Integer) session.getAttribute("session_user_Id");
			User user = getUser(userId);

			assert user != null;
			Pageable pageable = PageRequest.of(page, 5);
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
			model.addAttribute("title", "Error");
			logger.error(e.getMessage());
		}
		return "user/viewContacts";
	}

	public String updateContact(Integer Id, Model model, int page) {

		Contact contact = restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/getContact/"+Id, Contact.class).getBody();
		model.addAttribute("contact", contact);
		model.addAttribute("page", page);
		model.addAttribute("title", "Update Contact");

		return "user/updateContact";
	}

	public String updateContactHandler(Contact newContact, MultipartFile file, HttpSession session, Integer page) {
		try {

			Contact temp_contact = restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/getContact/"+newContact.getId(), Contact.class).getBody();
			if(!file.isEmpty()) {
				this.imageService.delete(Objects.requireNonNull(temp_contact).getImage().getPublic_id());
				newContact.setImage(imageService.upload(file));
			}else {
				newContact.setImage(Objects.requireNonNull(temp_contact).getImage());
			}

			Integer userId = (Integer) session.getAttribute("session_user_Id");
			User user = getUser(userId);

			assert user != null;
			newContact.setUserId(user.getId());

			newContact.setRegister(false);
			boolean temp = this.userRepository.getUserByEmail(newContact.getEmail()).isPresent();
			if(temp) newContact.setRegister(true);

			restTemplate.postForEntity("https://contactmanager-3c3x.onrender.com/updateContact", newContact, Contact.class);

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
			Contact temp_contact = restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/getContact/"+Id, Contact.class).getBody();
			this.imageService.delete(Objects.requireNonNull(temp_contact).getImage().getPublic_id());
			restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/deleteContact/"+Id, Void.class);
			session.setAttribute("message", new Message("Contact Deleted Successfully...", "success"));
		} catch (Exception e) {
			logger.info(e.getMessage());
			session.setAttribute("message", new Message("Error Deleting Contact...", "danger"));
		}
		return "redirect:/user/viewContacts/0";
	}

	public String invite(String username, Integer contactId, Integer page, HttpSession session, Model model) {
		try {
			Contact contact = restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/getContact/"+contactId, Contact.class).getBody();
			if(contact==null) {
				session.setAttribute("message", new Message("Contact not Exist..", "danger"));
			}else {
				String subject = "Invite : Contact Manager";
				String message = username+" invites you to : https://contactmanager-3c3x.onrender.com/";
				String to = contact.getEmail();
				boolean flag = this.emailService.sendEmail(subject, message, to);
				if (flag) {
					session.setAttribute("message", new Message("Invite Send to "+contact.getName(), "success"));
				}else {
					session.setAttribute("message", new Message("Contact Email Not Exists..", "danger"));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			session.setAttribute("message", new Message("Error Sending Invite..", "danger"));
		}
		model.addAttribute("title", "View Contacts");
		return "redirect:/user/viewContacts/"+page;
	}
}