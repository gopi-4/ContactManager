package com.smart.controllers;

import com.smart.entities.User;
import com.smart.service.DefaultService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DefaultController {

	@Autowired
	private DefaultService defaultService;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("title", "Smart Contact Manager");
		return "default/home";
	}

	@RequestMapping("/about")
	private String about(Model model) {
		model.addAttribute("title", "About");
		return "default/about";
	}

	@RequestMapping("/signUp")
	public String signup(Model model) {
		model.addAttribute("title", "SignUp - Smart Contact Manager");
		return "default/register";
	}

	@RequestMapping("/signIn")
	public String signIn(Model model) {
		model.addAttribute("title", "SignIn - Smart Contact Manager");
		return "default/authentication.html";
	}

	@RequestMapping("/forgotPassword")
	public String forgetPassWord(Model model) {
		model.addAttribute("title", "Change Password");
		return "default/forgotPassword.html";
	}

	@PostMapping(value = "/registration")
	public String register(@RequestParam("username") String username,
						 @RequestParam("email") String email,
						 @RequestParam("password") String password,
						 Model model, HttpSession session) {

		return defaultService.register(username, email, password, model, session);
	}

	@PostMapping("/passwordUpdate")
	public String passwordUpdate(@RequestParam("email") String username,
								 @RequestParam("password") String password,
								 HttpSession session,
								 Model model) {

		return defaultService.passwordUpdate(username, password,session,model);
	}
	
	@PostMapping("/otpVerify")
	public String OTPVerify(@RequestParam("OTP") int userOTP, HttpSession session, Model model) {
		
		return defaultService.OTPVerification(userOTP, session, model);
	}
}