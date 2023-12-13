package com.smart.controllers;

import com.smart.service.RedirectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/redirect")
public class RedirectController {

	@Autowired
	private RedirectService redirectService;

	@RequestMapping("/")
	private String redirect(Principal principal, Model model) {
		return redirectService.redirect(principal, model);
	}
	
}
