package com.smart.controllers;

import com.smart.entities.Messages;
import com.smart.entities.User;
import com.smart.service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Controller
@RequestMapping("/chat")
public class ChatController {

	private final Logger logger = LogManager.getLogger(ChatController.class);
	@Autowired
	private ChatService chatService;

	RestTemplate restTemplate = new RestTemplate();

	@ModelAttribute
	private void addCommonData(Model model, HttpSession session) {
		try {
			Integer userId = (Integer) session.getAttribute("session_user_Id");
			User user = restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/getUser/"+userId, User.class).getBody();
			model.addAttribute("admin", user);
			model.addAttribute("user", user);
		}catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	@GetMapping("/{contactId}")
	public String chat(@PathVariable("contactId") int Id, Model model) {
		return chatService.chat(Id, model);
	}
	
	@PostMapping("/getChat")
	public ResponseEntity<Optional<String>> getChat(@RequestParam("incoming") Integer incoming, HttpSession session){
		return chatService.getChat(incoming, session);
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
	public ResponseEntity<Optional<String>> adminGetChat(@RequestParam("incoming") Integer incoming, HttpSession session){
		return chatService.adminGetChat(incoming, session);
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
