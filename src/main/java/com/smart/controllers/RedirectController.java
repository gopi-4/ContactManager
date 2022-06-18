package com.smart.controllers;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Controller
@RequestMapping("/check")
public class RedirectController {

	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	private String where(Principal principal, HttpSession session) {
		User user = userRepository.getUserByEmail(principal.getName());
		session.setAttribute("user", user);
		if(user.getRole().equals("ROLE_ADMIN")) return "redirect:/admin/index";
		return "redirect:/user/index";
	}
	
}
