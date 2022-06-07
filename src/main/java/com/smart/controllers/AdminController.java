package com.smart.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helpers.Message;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserRepository userRepository;
	
	private long duration;
	
	@ModelAttribute
	private void addCommonData(Model model, Principal principal, HttpSession session) {

//		User admin = userRepository.getUserByEmail(principal.getName());
		User admin = (User) session.getAttribute("user");
		model.addAttribute("admin", admin);
	}
	
	@RequestMapping("/index")
	private String index(Model model, Principal principal, HttpSession session) {
		
//		User user = userRepository.getUserByEmail(principal.getName());
		User user = (User) session.getAttribute("user");
		user.setStatus(true);
		this.userRepository.save(user);
		session.setAttribute("admin", user);
		model.addAttribute("title", "Admin Dashboard");
		duration = System.currentTimeMillis();
		return "admin/index";
	}
	
	@GetMapping("/profile")
	public String profile(Model model) {

		model.addAttribute("title", "My Profile");
		return "admin/profile";
	}

//	@GetMapping("/updateUser/{Id}")
//	public String updateUser(@PathVariable("Id") Integer Id, Model model) {
//
//		model.addAttribute("title", "Update User");
//		User user = this.userRepository.getById(Id);
//		model.addAttribute("user", user);
//		return "admin/updateUser";
//	}

	@PostMapping("/process_updateUser")
	public String updateUserHandler(@ModelAttribute User user, HttpSession session) {

		try {
			this.userRepository.save(user);
			session.setAttribute("user", user);
			session.setAttribute("message", new Message("User updated successfully...", "success"));
		} catch (Exception e) {
			session.setAttribute("message", new Message("Error updating User...", "danger"));
			e.printStackTrace();
		}
		return "redirect:/user/profile";

	}
	
	@GetMapping("/viewUsers/{page}")
	private String viewUsers(@PathVariable("page") Integer page, Model model) {

		try {

			Pageable pageable = PageRequest.of(page, 10);
			Page<User> users = this.userRepository.findBySpecificRoles("ROLE_USER", pageable);
			if (page <= users.getTotalPages() && page >= 0) {
				model.addAttribute("users", users);
				model.addAttribute("currentPage", page);
				model.addAttribute("totalPages", users.getTotalPages()-1);
				model.addAttribute("title", "View Users");
			} else {
				model.addAttribute("title", "Error");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return "admin/viewUsers";
	}
	
	@GetMapping("/user/{Id}")
	public String showUserDetails(@PathVariable("Id") Integer Id, Model model, Principal principal) {

		Optional<User> optional = this.userRepository.findById(Id);
		User user = optional.get();
		model.addAttribute("user",user);
		return "admin/userDetails";
	}
	
	@GetMapping("/deleteUser/{Id}")
	public String deleteContact(@PathVariable("Id") Integer Id, HttpSession session) {

		try {
			User user = this.userRepository.getById(Id);
			this.userRepository.delete(user);
			session.setAttribute("message", new Message("Contact Deleted Successfully...", "success"));

		} catch (Exception e) {
			session.setAttribute("message", new Message("Error Deleting Contact...", "danger"));
			System.out.println(e.getMessage());
		}

		return "redirect:/admin/viewUsers/0";

	}
	
	@GetMapping("/signout")
	public String logout(Principal principal, HttpSession session) {
		
		User user = (User) session.getAttribute("user");
		user.setStatus(false);
		user.setCoins(user.getCoins() + (System.currentTimeMillis() - duration) / 360000000);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDateTime now = LocalDateTime.now();
		user.setDate(dtf.format(now));
		this.userRepository.save(user);
		session.setAttribute("user", user);
		return "redirect:/signout";
	}
}
