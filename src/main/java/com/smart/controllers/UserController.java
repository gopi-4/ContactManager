package com.smart.controllers;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

	private final Logger logger = LogManager.getLogger(UserController.class);
	@Autowired
	private UserService userService;

	@ModelAttribute private void addCommonData(Model model, Principal principal){
		try {
			model.addAttribute("user", this.userService.getUserByEmail(principal.getName()));
		}catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@GetMapping("/index")
	private String index(Model model) {
		model.addAttribute("title", "User Dashboard");
		return "user/weather";
	}

	@GetMapping("/addContact")
	private String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "user/addContact";
	}

	@GetMapping("/profile")
	public String profile(Model model) {
		model.addAttribute("title", "My Profile");
		return "user/profile";
	}

	@GetMapping("/updateUser/{Id}")
	public String updateUser(@PathVariable("Id") Integer Id, Model model) {
		model.addAttribute("title", "Update User");
		return "user/updateUser";
	}

	@PostMapping("/processData")
	private String addContact(@ModelAttribute() Contact contact, @RequestParam("profileImage") MultipartFile file, Model model, Principal principal, HttpSession session) {
		return userService.addContact(contact, file, model, principal, session);
	}

	@GetMapping("/viewContacts/{page}")
	private String viewContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		return userService.viewContacts(page, model, principal);
	}

	@GetMapping("/updateContact/{Id}/{currentPage}")
	public String updateContact(@PathVariable("Id") Integer Id, Model model, @PathVariable("currentPage") Integer page) {
		return userService.updateContact(Id, model, page);
	}

	@PostMapping("/{page}/processUpdateContact")
	public String updateContactHandler(@ModelAttribute Contact newContact,
									   @RequestParam("profileImage") MultipartFile file,
									   Principal principal,
									   HttpSession session, @PathVariable("page") Integer page) {

		return userService.updateContactHandler(newContact, file, principal, session, page);
	}

	@GetMapping("/delete/{Id}")
	public String deleteContact(@PathVariable("Id") Integer Id, HttpSession session) {
		return userService.deleteContact(Id, session);
	}

	@PostMapping("/processUpdateUser")
	public String updateUserHandler(@ModelAttribute User user,
									@RequestParam("profileImageUser") MultipartFile file,
									HttpSession session) {

		return userService.updateUserHandler(user, file, session);
	}
	
	@GetMapping("/invite/{username}/{contactId}/{page}")
	public String invite(@PathVariable("username") String username, @PathVariable("contactId") Integer Id, @PathVariable("page") Integer page, HttpSession session, Model model) {
		return userService.invite(username, Id, page, session, model);
	}

	@GetMapping("/signOut")
	public String logout(Principal principal) {
		return "redirect:/signOut";
	}

}
