package com.smart.service;

import com.smart.dto.Message;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

	private final Logger logger = LogManager.getLogger(UserService.class);
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private ImageService imageService;
	@Autowired
	private SessionRegistry sessionRegistry;
	RestTemplate restTemplate = new RestTemplate();

	public String updateUserHandler(String username, String about, MultipartFile file, HttpSession session) {
		try {
			User user = (User) session.getAttribute("session_user");

			if(!file.isEmpty()) {
				this.imageService.delete(user.getImage().public_id);
				user.setImage(imageService.upload(file));
			}
			if (username!=null) user.setName(username);
			if (about!=null) user.setAbout(about);

			session.setAttribute("session_user", user);

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

			contact.setName(contact.getName().toLowerCase());
			User user = (User) session.getAttribute("session_user");

			if(file.isEmpty()) {
				contact.setImage(imageService.getDefault());
			}else {
				contact.setImage(imageService.upload(file));
			}

			boolean temp = this.userRepository.getUserByEmail(contact.getEmail()).isPresent();
			if(temp) contact.setRegister(true);

			contact.setUserId(user.getId());

//			this.contactRepository.save(contact);
			List<Contact> contacts = (List<Contact>) session.getAttribute("contacts");
			contacts.add(contact);
			session.setAttribute("contacts", contacts);

			logger.info("Contact Saved.");
			session.setAttribute("message", new Message("Contact Added Successfully..", "success"));
		} catch (Exception e) {
			logger.error(e.getMessage());
			session.setAttribute("message", new Message("Something Went Wrong..", "danger"));
		}
		model.addAttribute("title", "Add Contact");
		return "redirect:addContact";
	}

	public String viewContacts(Integer page, Model model, HttpSession session) {

		model.addAttribute("title", "Error");
		try {

			List<Integer> online_users = sessionRegistry.getAllPrincipals().stream()
					.filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
					.map(Object::hashCode)
					.toList();

			Page<Contact> contacts = StaticServices.getContacts(page, session, online_users);

			if (page <= contacts.getTotalPages() && page >= 0) {
				model.addAttribute("contacts", contacts);
				model.addAttribute("currentPage", page);
				model.addAttribute("totalPages", contacts.getTotalPages() - 1);
				model.addAttribute("title", "View Contacts");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "user/viewContacts";
	}

	public String updateContact(Integer index, Model model, int page, HttpSession session) {

//		Contact contact = restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/getContact/"+Id, Contact.class).getBody();
		List<Contact> contacts = (List<Contact>) session.getAttribute("contacts");
		session.setAttribute("temp_contact", contacts.get(index));
		model.addAttribute("contact", contacts.get(index));
		model.addAttribute("page", page);
		model.addAttribute("title", "Update Contact");

		return "user/updateContact";
	}

	public String updateContactHandler(Contact newContact, MultipartFile file, HttpSession session, Integer page) {
		try {

			Contact contact = (Contact) session.getAttribute("temp_contact");
			if(!file.isEmpty()) {
				this.imageService.delete(Objects.requireNonNull(contact).getImage().getPublic_id());
				contact.setImage(imageService.upload(file));
			}
//			else {
//				contact.setImage(Objects.requireNonNull(contact).getImage());
//			}

//			User user = (User) session.getAttribute("session_user");

//			newContact.setUserId(user.getId());
			contact.setName(newContact.getName());
			if(newContact.getEmail()!=null) {
				contact.setEmail(newContact.getEmail());
				contact.setRegister(false);
				boolean temp = this.userRepository.getUserByEmail(newContact.getEmail()).isPresent();
				if(temp) newContact.setRegister(true);
			}
			contact.setPhone(newContact.getPhone());
			if (newContact.getDescription()!=null) contact.setDescription(newContact.getDescription());

//			restTemplate.postForEntity("https://contactmanager-3c3x.onrender.com/updateContact", newContact, Contact.class);

			logger.info("Contact Updated.");
			session.setAttribute("message", new Message("Your Contact updated successfully...", "success"));
		} catch (Exception e) {
			session.setAttribute("message", new Message("Error updating Contact...", "danger"));
			logger.error(e.getMessage());
		}
		return "redirect:/user/viewContacts/" + page;
	}

	public String deleteContact(Integer index, HttpSession session) {
		try {
			List<Contact> contacts = (List<Contact>) session.getAttribute("contacts");
			Contact contact = contacts.get(index);
			this.imageService.delete(Objects.requireNonNull(contact).getImage().getPublic_id());
			contacts.remove(contact);
			session.setAttribute("contacts", contacts);
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