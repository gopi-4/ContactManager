package com.smart.controllers;

import com.smart.service.AdminService;
import com.smart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private UserService userService;

	@ModelAttribute
	private void addCommonData(Model model, Principal principal) {
		model.addAttribute("admin", this.userService.getUserByEmail(principal.getName()));
	}
	@GetMapping("/index")
	private String index(Model model) {
		model.addAttribute("title", "Admin Dashboard");
		return "admin/index";
	}
	
	@GetMapping("/profile")
	public String profile(Model model) {
		model.addAttribute("title", "My Profile");
		return "admin/profile";
	}

	@GetMapping("/viewUsers/{page}")
	public String viewUsers(@PathVariable("page") Integer page, Model model) {
		return adminService.viewUsers(page, model);
	}
	
	@GetMapping("/user/{Id}")
	public String showUserDetails(@PathVariable("Id") Integer Id, Model model) {
		return adminService.showUserDetails(Id, model);
	}
	
	@GetMapping("/deleteUser/{Id}")
	public String deleteContact(@PathVariable("Id") Integer Id, HttpSession session) {
		return adminService.deleteContact(Id, session);

	}
	
	@GetMapping("/signOut")
	public String logout(Principal principal) {
		return adminService.logout(principal);
	}
}
