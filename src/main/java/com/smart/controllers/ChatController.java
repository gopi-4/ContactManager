package com.smart.controllers;

import com.smart.entities.Messages;
import com.smart.entities.User;
import com.smart.repository.UserRepository;
import com.smart.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/chat")
public class ChatController {
	
	@Autowired
	private ChatService chatService;

	@Autowired
	private UserRepository userRepository;

	@ModelAttribute
	private void addCommonData(Model model, Principal principal) {
		User admin = userRepository.getUserByEmail(principal.getName()).orElse(null);
		model.addAttribute("admin", admin);
	}
	
	@GetMapping("/{contactId}")
	public String chat(@PathVariable("contactId") int Id, Model model) {
		return chatService.chat(Id, model);
	}
	
	@PostMapping("/getChat")
	public ResponseEntity<Optional<String>> getChat(@RequestParam("incoming") Integer incoming, Principal principal){
		return chatService.getChat(incoming, principal);
		
	}
	
	@PostMapping("/insertChat")
	public ResponseEntity<Messages> insertChat(@RequestParam("outgoing") Integer outgoing, @RequestParam("incoming") Integer incoming, @RequestParam("message") String message){
		return chatService.insertChat(outgoing, incoming, message);
	}
	
	@GetMapping("/admin/{userId}")
	public String adminChat(@PathVariable("userId") int Id, Model model) {
		return chatService.adminChat(Id, model);
	}
	
	@PostMapping("/admin/getChat")
	public ResponseEntity<Optional<String>> adminGetChat(@RequestParam("incoming") Integer incoming, Principal principal){
		return chatService.adminGetChat(incoming, principal);
		
	}
	
	@PostMapping("/admin/insertChat")
	public ResponseEntity<Messages> adminInsertChat(@RequestParam("outgoing") Integer outgoing, @RequestParam("incoming") Integer incoming, @RequestParam("message") String message){
		return chatService.adminInsertChat(outgoing, incoming, message);
	}
	
	@GetMapping("/chatAdmin")
	public String chatAdmin(Model model) {
		return chatService.chatAdmin(model);
	}

}
