package com.smart.controllers;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.service.UserService;
import jakarta.annotation.Nullable;
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

	@ModelAttribute
	private void addCommonData(Model model, HttpSession session){
		try {
			User user = (User) session.getAttribute("session_user");
			model.addAttribute("user", user);
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
	private String addContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
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
	private String addContact(@ModelAttribute() Contact contact, @RequestParam("profileImage") MultipartFile file, Model model, HttpSession session) {
		return userService.addContact(contact, file, model, session);
	}

	@GetMapping("/viewContacts/{page}")
	private String viewContacts(@PathVariable("page") Integer page, Model model, HttpSession session) {
		return userService.viewContacts(page, model, session);
	}

	@GetMapping("/updateContact/{index}/{page}")
	public String updateContact(@PathVariable("index") Integer index, Model model, @PathVariable("page") Integer page, HttpSession session) {
		return userService.updateContact(index, model, page, session);
	}

	@PostMapping("/processUpdateContact/{page}")
	public String updateContactHandler(@ModelAttribute Contact newContact,
									   @RequestParam("profileImage") MultipartFile file,
									   HttpSession session, @PathVariable("page") Integer page) {

		return userService.updateContactHandler(newContact, file, session, page);
	}

	@GetMapping("/delete/{index}")
	public String deleteContact(@PathVariable("index") Integer index, HttpSession session) {
		return userService.deleteContact(index, session);
	}

	@PostMapping("/processUpdateUser")
	public String updateUserHandler(@RequestParam("username") @Nullable String username,
									@RequestParam("about") @Nullable String about,
									@RequestParam("profileImageUser") MultipartFile file,
									HttpSession session) {

		return userService.updateUserHandler(username, about, file, session);
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
