package com.smart.controllers;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@ModelAttribute private void addCommonData(Model model, Principal principal){
		model.addAttribute("user", this.userService.getUserByEmail(principal.getName()));
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

	@GetMapping("/delete/{Id}")
	public String deleteContact(@PathVariable("Id") Integer Id, HttpSession session) {
		return userService.deleteContact(Id, session);
	}

	@PostMapping("/{page}/processUpdateContact")
	public String updateContactHandler(@ModelAttribute Contact newContact,
									   @RequestParam("profileImage") MultipartFile file,
									   Principal principal,
									   HttpSession session, @PathVariable("page") Integer page) {

		return userService.updateContactHandler(newContact, file, principal, session, page);
	}

	@PostMapping("/processUpdateUser")
	public String updateUserHandler(@ModelAttribute User user,
									@RequestParam("profileImageUser") MultipartFile file,
									HttpSession session) {

		return userService.updateUserHandler(user, file, session);
	}

	@GetMapping("/signOut")
	public String logout(Principal principal) {
		return userService.logout(principal);
	}
	
	@GetMapping("/invite/{username}/{contactId}/{page}")
	public String passwordUpdate(@PathVariable("username") String username, @PathVariable("contactId") Integer Id, @PathVariable("page") Integer page, HttpSession session, Model model) {
		return userService.passwordUpdate(username, Id, page, session, model);
	}

}
