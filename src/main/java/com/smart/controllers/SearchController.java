package com.smart.controllers;

import com.smart.service.SearchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/search")
public class SearchController {

	@Autowired
	private SearchService searchService;
	
	@GetMapping("/contact/{page}")
	public String searchContact(@PathVariable("page") Integer page, @RequestParam("search-input") String contactName, HttpSession session, Model model){
		return searchService.searchContact(page, contactName, session, model);
	}
	
	@GetMapping("/user/{page}")
	public String searchUser(@PathVariable("page") Integer page, @RequestParam("search-input") String userName, HttpSession session, Model model){
		return searchService.searchUser(page, userName, session, model);
	}
}
