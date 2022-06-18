package com.smart.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.ContactRepository;
import com.smart.dao.MessageRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.Messages;
import com.smart.entities.User;

@Controller
@RequestMapping("/chat")
public class ChatController {
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@ModelAttribute
	private void addCommonData(Model model, Principal principal) {

		User user = this.userRepository.getUserByEmail(principal.getName());
		model.addAttribute("user", user);
	}
	
	@GetMapping("/{contactId}")
	public String chat(@PathVariable("contactId") int contactId, Model model) {
		
		Contact contact = this.contactRepository.getById(contactId);
		if(contact==null || contact.getUnique_id()==0) return "redirect:/user/view_contacts/0";
		model.addAttribute("contact", contact);
		model.addAttribute("status", this.userRepository.getUserByEmail(contact.getEmail()).getStatus());
		return "chat";
	}
	
	@PostMapping("/getChat")
	public ResponseEntity<Optional<String>> getChat(@RequestParam("incoming_id") Integer incoming_id, Principal principal){
		
		User user = this.userRepository.getUserByEmail(principal.getName());
		int outgoing_id = this.userRepository.getUserByEmail(user.getEmail()).getId();
		StringBuffer sBuffer = new StringBuffer();
		
		List<Messages> messages = this.messageRepository.findMessages(outgoing_id, incoming_id);
		if(messages.size()>0) {
			for(int i=0;i<messages.size();i++) {
				if(messages.get(i).getOutgoing_msg_id()==outgoing_id) {
					sBuffer.append("<div class"+"=chat-outgoing"+"><div class="+"details"+"><p>"+messages.get(i).getMsg()+"</p></div></div>");
				}else {
					sBuffer.append("<div class=chat-incoming><div class=details><p>"+messages.get(i).getMsg()+ "</p></div></div>");
				}
			}
		}else {
			sBuffer.append("<div class=text>No messages are available. Once you send message they will appear here.</div>");
		}
		return ResponseEntity.ok(Optional.of(sBuffer.toString()));
		
	}
	
	@PostMapping("/insertChat")
	public ResponseEntity<Messages> insertChat(@RequestParam("outgoing_id") Integer outgoing_id, @RequestParam("incoming_id") Integer incoming_id, @RequestParam("message") String message){
		
		Messages messages = null;
		if(message.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		messages = this.messageRepository.save(new Messages(incoming_id, outgoing_id, message));  
		return ResponseEntity.of(Optional.of(messages));
	}
	
	@GetMapping("/admin/{userId}")
	public String adminChat(@PathVariable("userId") int userId, Model model, Principal principal) {
		
		User user = this.userRepository.getById(userId);
		if(user==null) return "redirect:/admin/viewUsers/0";
		System.out.println(user.getName());
		model.addAttribute("contact", user);
		model.addAttribute("status", user.getStatus());
		return "admin/chat";
	}
	
	@PostMapping("/admin/getChat")
	public ResponseEntity<Optional<String>> admingetChat(@RequestParam("incoming_id") Integer incoming_id, Principal principal){
		
		User user = this.userRepository.getUserByEmail(principal.getName());
		int outgoing_id = user.getId();
		StringBuffer sBuffer = new StringBuffer();
		
		List<Messages> messages = this.messageRepository.findMessages(outgoing_id, incoming_id);
		if(messages.size()>0) {
			for(int i=0;i<messages.size();i++) {
				if(messages.get(i).getOutgoing_msg_id()==outgoing_id) {
					sBuffer.append("<div class"+"=chat-outgoing"+"><div class="+"details"+"><p>"+messages.get(i).getMsg()+"</p></div></div>");
				}else {
					sBuffer.append("<div class=chat-incoming><div class=details><p>"+messages.get(i).getMsg()+ "</p></div></div>");
				}
			}
		}else {
			sBuffer.append("<div class=text>No messages are available. Once you send message they will appear here.</div>");
		}
		return ResponseEntity.ok(Optional.of(sBuffer.toString()));
		
	}
	
	@PostMapping("/admin/insertChat")
	public ResponseEntity<Messages> admininsertChat(@RequestParam("outgoing_id") Integer outgoing_id, @RequestParam("incoming_id") Integer incoming_id, @RequestParam("message") String message){
		
		Messages messages = null;
		if(message.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		messages = this.messageRepository.save(new Messages(incoming_id, outgoing_id, message));  
		return ResponseEntity.of(Optional.of(messages));
	}
	
	@GetMapping("/chatAdmin")
	public String chatAdmin(Model model) {
		
		String ADMIN_EMAIL = System.getenv("ADMIN_EMAIL");
		User user = this.userRepository.getUserByEmail(ADMIN_EMAIL);
		if(user==null) return "redirect:/user/index";
		model.addAttribute("contact", user);
		model.addAttribute("status", user.getStatus());
		return "admin/chat";
	}

}
