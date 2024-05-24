package com.smart.controllers;

import com.smart.entities.User;
import com.smart.service.DefaultService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DefaultController {

	@Autowired
	private DefaultService defaultService;

	@RequestMapping("/")
	public String home(Model model, HttpSession session) {
		model.addAttribute("user", new User());
		model.addAttribute("title", "Smart Contact Manager");
		if(session.getAttribute("session_user")!=null) return "redirect:/user/index";
		return "default/home";
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

	@RequestMapping("/verify")
	public String getOTPPage(Model model) {
		model.addAttribute("title", "Verification");
		return "default/otp.html";
	}

	@PostMapping(value = "/registration")
	public String register(@RequestParam("username") String username,
						 @RequestParam("email") String email,
						 @RequestParam("password") String password,
						 Model model, HttpSession session) {

		return this.defaultService.register(username, email, password, model, session);
	}

	@PostMapping("/generateOTP")
	public String generateOTP(@RequestParam("email") String username,
								 @RequestParam("password") String password,
								 HttpSession session,
								 Model model) {

		return this.defaultService.generateOTP(username, password,session,model);
	}
	
	@PostMapping("/verification")
	public String verification(@RequestParam("OTP") int userOTP, HttpSession session, Model model) {
		return this.defaultService.verification(userOTP, session, model);
	}

	@RequestMapping("/confirm-account")
	public String confirmUserAccount(@RequestParam("token") String Token, HttpSession session) {
		return this.defaultService.confirmUserAccount(Token, session);
	}
}