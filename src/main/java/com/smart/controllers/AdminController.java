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

	private final Logger logger = LogManager.getLogger(UserController.class);
	@Autowired
	private AdminService adminService;

	RestTemplate restTemplate = new RestTemplate();

	@ModelAttribute
	private void addCommonData(Model model, HttpSession session) {
		try {
			Integer userId = (Integer) session.getAttribute("session_user_Id");
			User user = restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/getUser/"+userId, User.class).getBody();
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
	public String viewUsers(@PathVariable("page") Integer page, Model model) {
		return adminService.viewUsers(page, model);
	}
	
	@GetMapping("/deleteUser/{Id}")
	public String deleteUser(@PathVariable("Id") Integer Id, HttpSession session) {
		return adminService.deleteUser(Id, session);
	}
	
	@GetMapping("/signOut")
	public String logout(Principal principal) {
		return "redirect:/signOut";
	}
}