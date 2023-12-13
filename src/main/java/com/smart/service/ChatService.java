package com.smart.service;

import com.smart.entities.Contact;
import com.smart.entities.Messages;
import com.smart.entities.User;
import com.smart.repository.ContactRepository;
import com.smart.repository.MessageRepository;
import com.smart.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ChatService {

	private final Logger logger = LogManager.getLogger(ChatService.class);
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MessageRepository messageRepository;

	public String chat(int Id, Model model) {
		
		Contact contact = this.contactRepository.findById(Id).orElse(null);
		if(contact==null) return "redirect:/user/viewContacts/0";
		model.addAttribute("contact", contact);
		model.addAttribute("status", Objects.requireNonNull(this.userRepository.getUserByEmail(contact.getEmail()).orElse(null)).isStatus());
		return "chat";
	}

	public ResponseEntity<Optional<String>> getChat(Integer incoming, Principal principal){

		User user = userRepository.getUserByEmail(principal.getName()).orElse(null);

		assert user != null;
		int outgoing = user.getId();
		StringBuilder sb = new StringBuilder();
		
		List<Messages> messages = this.messageRepository.findMessages(outgoing, incoming);
		if(messages.size()>0) {
			for (Messages message : messages) {
				if (message.getOutgoing() == outgoing) {
					sb.append("<div class" + "=chat-outgoing" + "><div class=" + "details" + "><p>").append(message.getMsg()).append("</p></div></div>");
				} else {
					sb.append("<div class=chat-incoming><div class=details><p>").append(message.getMsg()).append("</p></div></div>");
				}
			}
		}else {
			sb.append("<div class=text>No messages are available. Once you send message they will appear here.</div>");
		}
		return ResponseEntity.ok(Optional.of(sb.toString()));
		
	}

	public ResponseEntity<Messages> insertChat(Integer outgoing, Integer incoming, String message){
		
		Messages messages;
		if(message.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		messages = this.messageRepository.save(new Messages(incoming, outgoing, message));
		return ResponseEntity.of(Optional.of(messages));
	}

	public String adminChat(int Id, Model model) {
		
		User user = this.userRepository.findById(Id).orElse(null);
		if(user==null) return "redirect:/admin/viewUsers/0";
		model.addAttribute("contact", user);
		model.addAttribute("status", user.isStatus());
		return "admin/chat";
	}

	public ResponseEntity<Optional<String>> adminGetChat(Integer incoming, Principal principal){

		User user = userRepository.getUserByEmail(principal.getName()).orElse(null);
		assert user != null;
		int outgoing = user.getId();
		StringBuilder sb = new StringBuilder();
		
		List<Messages> messages = this.messageRepository.findMessages(outgoing, incoming);
		if(messages.size()>0) {
			for (Messages message : messages) {
				if (message.getOutgoing() == outgoing) {
					sb.append("<div class" + "=chat-outgoing" + "><div class=" + "details" + "><p>").append(message.getMsg()).append("</p></div></div>");
				} else {
					sb.append("<div class=chat-incoming><div class=details><p>").append(message.getMsg()).append("</p></div></div>");
				}
			}
		}else {
			sb.append("<div class=text>No messages are available. Once you send message they will appear here.</div>");
		}
		return ResponseEntity.ok(Optional.of(sb.toString()));
		
	}

	public ResponseEntity<Messages> adminInsertChat(Integer outgoing, Integer incoming, String message){
		Messages messages;
		if(message.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		messages = this.messageRepository.save(new Messages(incoming, outgoing, message));
		return ResponseEntity.of(Optional.of(messages));
	}

	public String chatAdmin(Model model) {
		User user = userRepository.getUserByEmail("2019058@iiitdmj.ac.in").orElse(null);
		if(user==null) return "redirect:/user/index";
		model.addAttribute("contact", user);
		model.addAttribute("status", user.isStatus());
		return "admin/chat";
	}

}
