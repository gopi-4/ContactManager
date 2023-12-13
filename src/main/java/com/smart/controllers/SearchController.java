package com.smart.controllers;

import com.smart.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/search")
public class SearchController {

	@Autowired
	private SearchService searchService;
	
	@GetMapping("/contact/{page}")
	public String search(@PathVariable("page") Integer page, @RequestParam("search-input") String contactName, Principal principal, Model model){
		return searchService.search(page, contactName, principal, model);
	}
	
	@GetMapping("/user/{page}")
	public String searchUser(@PathVariable("page") Integer page, @RequestParam("search-input") String userName, Principal principal, Model model){
		return searchService.searchUser(page, userName, principal, model);
	}
}
