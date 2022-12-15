package com.smart.controllers;

import java.io.FileInputStream;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Book;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helpers.Message;
import com.smart.service.EmailService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;

	long duration = 0;

	@ModelAttribute 
	private void addCommonData(Model model, Principal principal){
	  
//		System.out.println(principal.getName());
		User user = this.userRepository.getUserByEmail(principal.getName());
		model.addAttribute("user", user); 
	}

	@GetMapping("/index")
	private String index(Model model) {

		model.addAttribute("title", "User Dashboard");
		duration = System.currentTimeMillis();
		return "normal/wheather";
	}

	@GetMapping("/add_contact")
	private String openAddContactForm(Model model) {

		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact";
	}

	@PostMapping("/process_data")
	private String process_data(@ModelAttribute() Contact contact, @RequestParam("profileImage") MultipartFile file, Model model, Principal principal, HttpSession session) {

		try {

			String emailString = principal.getName();
			User user = userRepository.getUserByEmail(emailString);

			if(file.isEmpty()) {
				FileInputStream fileInputStream = new FileInputStream("src/main/resources/static/images/default.jpg");
				contact.setImage(Base64Utils.encodeToString(fileInputStream.readAllBytes()));
				fileInputStream.close();
			}else {
				contact.setImage(Base64Utils.encodeToString(file.getBytes()));
			}

			contact.setUser(user);
			user.getContacts().add(contact);

			User tempUser = this.userRepository.getUserByEmail(contact.getEmail());
			if (tempUser != null)
				contact.setUnique_id(tempUser.getId());
			else
				contact.setUnique_id(0);

			this.userRepository.save(user);

			session.setAttribute("message", new Message("Contact Added Successfully..", "success"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			if (e.getMessage().equals(
					"Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Error while committing the transaction")) {
				session.setAttribute("message", new Message("Enter Correct E-mail or Phone..", "danger"));
			}else if(e.getMessage().equals("could not execute statement; SQL [n/a]; constraint [contacts.UK_728mksvqr0n907kujew6p3jc0]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement")){
				session.setAttribute("message", new Message("Contact Exists..", "danger"));
			}else {
				session.setAttribute("message", new Message("Something Went Wrong..", "danger"));
			}

		}
		model.addAttribute("title", "Add Contact");
		return "normal/add_contact";
	}

	@GetMapping("/view_contacts/{page}")
	private String viewContacts(@PathVariable("page") Integer page, Model model, Principal principal) {

		try {

			String userEmail = principal.getName();
			User user = this.userRepository.getUserByEmail(userEmail);
			Pageable pageable = PageRequest.of(page, 5);
			Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
			List<User> users = this.userRepository.findAll();
			List<String> userEmailsList = new ArrayList<>();
			for (User user2 : users)
				userEmailsList.add(user2.getEmail());
			for (Contact contact : contacts) {
				User user1 = this.userRepository.getUserByEmail(contact.getEmail());
				if (user1!=null) {
					contact.setStatus(user1.getStatus() ? "Online" : "Offline");
				} else {
					contact.setStatus("Not Registered");
				}
			}
			if (page <= contacts.getTotalPages() && page >= 0) {
				model.addAttribute("contacts", contacts);
				model.addAttribute("currentPage", page);
				model.addAttribute("totalPages", contacts.getTotalPages() - 1);
				model.addAttribute("title", "View Contacts");
			} else {
				model.addAttribute("title", "Error");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return "normal/view_contacts";
	}

	@GetMapping("/contact/{Cid}")
	public String showContactDetails(@PathVariable("Cid") Integer Cid, Model model, Principal principal) {

		Optional<Contact> optional = this.contactRepository.findById(Cid);
		Contact contact = optional.get();
		String userEmail = principal.getName();
		User user = this.userRepository.getUserByEmail(userEmail);

		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName() + " Details");

		} else {
			model.addAttribute("title", "Unauthorized");
		}

		return "normal/contact_details";
	}

	@GetMapping("/delete/{Cid}")
	public String deleteContact(@PathVariable("Cid") Integer Cid, Principal principal, HttpSession session) {

		try {
			String userEmail = principal.getName();
			User user = this.userRepository.getUserByEmail(userEmail);

			Contact contact = this.contactRepository.findById(Cid).get();

			if (user.getId() == contact.getUser().getId()) {

				user.getContacts().remove(contact);
				this.userRepository.save(user);
				session.setAttribute("message", new Message("Contact Deleted Successfully...", "success"));
			} else {
				session.setAttribute("message", new Message("Error Deleting Contact...", "danger"));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return "redirect:/user/view_contacts/0";

	}

	@GetMapping("/updateContact/{Cid}")
	public String updateContact(@PathVariable("Cid") Integer Cid, Model model, Principal principal) {

		String userEmail = principal.getName();
		User user = this.userRepository.getUserByEmail(userEmail);
		Contact contact = this.contactRepository.findById(Cid).get();

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		model.addAttribute("title", "Update Contact");

		return "normal/update_contact";
	}

	@PostMapping("/process_updateContact")
	public String updateContactHandler(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, Model model, Principal principal, HttpSession session) {

		try {

			if(!file.isEmpty()) {
				contact.setImage(Base64Utils.encodeToString(file.getBytes()));
			}
			User user = this.userRepository.getUserByEmail(principal.getName());
			contact.setUser(user);

			User tempUser = this.userRepository.getUserByEmail(contact.getEmail());
			if (tempUser != null)
				contact.setUnique_id(tempUser.getId());
			else
				contact.setUnique_id(0);

			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Your Contact updated successfully...", "success"));
		} catch (Exception e) {
			session.setAttribute("message", new Message("Error updating Contact...", "danger"));
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return "redirect:/user/contact/" + contact.getCid();
	}

	@GetMapping("/profile")
	public String profile(Model model) {
		model.addAttribute("title", "My Profile");
		return "normal/profile";
	}

	@GetMapping("/updateUser/{Id}")
	public String updateUser(@PathVariable("Id") Integer Id, Model model) {

		model.addAttribute("title", "Update User");
		return "normal/update_user";
	}

	@PostMapping("/process_updateUser")
	public String updateUserHandler(@ModelAttribute User user, @RequestParam("profileImageUser") MultipartFile file,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {
			if (agreement) {
				if(!file.isEmpty()) {
					user.setImage(Base64Utils.encodeToString(file.getBytes()));
				}
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				this.userRepository.save(user);
				session.setAttribute("message", new Message("User updated successfully...", "success"));
			} else {
				System.out.println("Please Accepts term's and condition's.");
				throw new Exception("You have not agreed term's and condition's.");
			}
		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "danger"));
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return "redirect:/user/profile";
	}

	@GetMapping("/signout")
	public String logout(Principal principal) {

		User user = userRepository.getUserByEmail(principal.getName());
		user.setStatus(false);
		user.setCoins(user.getCoins() + (System.currentTimeMillis() - duration) / 360000000);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDateTime now = LocalDateTime.now();
		user.setDate(dtf.format(now));
		this.userRepository.save(user);
		return "redirect:/signout";
	}

	@RequestMapping("/books")
	public String BookShop(Model model) {

		try {
			String urlString = "https://book-recommender-v1.herokuapp.com/popular";
			RestTemplate restTemplate = new RestTemplate();
			List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
			MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
			converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
			messageConverters.add(converter);
			restTemplate.setMessageConverters(messageConverters);
			ResponseEntity<List<Book>> responseEntity = restTemplate.exchange(urlString, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Book>>() {
					});
			List<Book> books = responseEntity.getBody();
			model.addAttribute("books", books);
			model.addAttribute("book", "Most Popular Books.");
		} catch (Exception e) {
			System.out.println(e);
		}
		model.addAttribute("title", "Books");
		return "booksPage";
	}

	@PostMapping("/recommend_books")
	public String BookShop(Model model, @RequestParam("bookName") String bookName) {

		try {
			String urlString = "https://book-recommender-v1.herokuapp.com/recommend_books/" + bookName;
			RestTemplate restTemplate = new RestTemplate();
			List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
			MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
			converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
			messageConverters.add(converter);
			restTemplate.setMessageConverters(messageConverters);
			ResponseEntity<List<Book>> responseEntity = restTemplate.exchange(urlString, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Book>>() {
					});
			List<Book> books = responseEntity.getBody();
			model.addAttribute("books", books);
			model.addAttribute("book", bookName);
		} catch (Exception e) {
			System.out.println(e);
		}
		model.addAttribute("title", "Books");
		return "booksPage";
	}
	
	@GetMapping("/invite/{username}/{contactId}/{page}")
	public String passwordUpdate(@PathVariable("username") String username, @PathVariable("contactId") Integer contactId, @PathVariable("page") Integer page, HttpSession session, Model model) {
		model.addAttribute("title", "Change Password");
		try {
			Contact contact = this.contactRepository.getById(contactId);
			if(contact==null) {
				session.setAttribute("message", new Message("Contact not Exist..", "danger"));
				model.addAttribute("title", "View Contacts");
			}else {
				
				String subject = "Invite : Smart Contact Manager";
				String message = username+" invites you to : https://scm-v1.herokuapp.com/";
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
			System.out.println(e.getMessage());
			session.setAttribute("message", new Message("Error Sending Invite..", "danger"));
		}		
		return "redirect:/user/view_contacts/"+page;
	}

}
