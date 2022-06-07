package com.smart.controllers;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.oauth2.CustomOAuth2User;

@Controller
@RequestMapping("/check")
public class RedirectController {

	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/social")
	private String where(Principal principal, Authentication authentication, HttpSession session) {
		CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
		String emailString = oAuth2User.getEmail();
//		System.out.println(emailString);
		User user = userRepository.getUserByEmail(emailString);
		session.setAttribute("user", user);
		if(user.getRole().equals("ROLE_ADMIN")) return "redirect:/admin/index";
		return "redirect:/user/index";
	}
	
	@RequestMapping("/")
	private String where(Principal principal, HttpSession session) {
		User user = userRepository.getUserByEmail(principal.getName());
		session.setAttribute("user", user);
		if(user.getRole().equals("ROLE_ADMIN")) return "redirect:/admin/index";
		return "redirect:/user/index";
	}
	
}
