package com.smart.controllers;

import com.smart.entities.User;
import com.smart.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final Logger logger = LogManager.getLogger(AdminController.class);
	@Autowired
	private AdminService adminService;
	RestTemplate restTemplate = new RestTemplate();

	@ModelAttribute
	private void addCommonData(Model model, HttpSession session) {
		try {
			User user = (User) session.getAttribute("session_user");
			model.addAttribute("admin", user);
		}catch (Exception e) {
			logger.error(e.getMessage());
		}
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
	public String viewUsers(@PathVariable("page") Integer page, Model model, HttpSession session) {
		return adminService.viewUsers(page, model, session);
	}
	
	@GetMapping("/deleteUser/{index}")
	public String deleteUser(@PathVariable("index") Integer index, HttpSession session) {
		return adminService.deleteUser(index, session);
	}
	
	@GetMapping("/signOut")
	public String logout(Principal principal) {
		return "redirect:/signOut";
	}
}