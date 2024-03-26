package com.smart.service;

import com.smart.entities.Contact;
import com.smart.entities.Message;
import com.smart.entities.User;
import com.smart.repository.MessageRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

	private final Logger logger = LogManager.getLogger(ChatService.class);

	@Autowired
	private MessageRepository messageRepository;

	RestTemplate restTemplate = new RestTemplate();

	public String chat(int Id, Model model) {
		
		Contact contact = restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/getContact/"+Id, Contact.class).getBody();
		if(contact==null) return "redirect:/user/viewContacts/0";
		model.addAttribute("contact", contact);
		return "default/chat";
	}

	public ResponseEntity<Optional<String>> getChat(Integer incoming, HttpSession session){

		User user = (User) session.getAttribute("session_user");

		int outgoing = user.getId();
		StringBuilder sb = new StringBuilder();
		
		List<Message> messages = this.messageRepository.findMessages(outgoing, incoming);
		if(messages.size()>0) {
			for (Message message : messages) {
				if (message.getOutgoing() == outgoing) {
					sb.append("<div class=chat-outgoing><div class=details><p>").append(message.getMsg()).append("</p></div></div>");
				} else {
					sb.append("<div class=chat-incoming><div class=details><p>").append(message.getMsg()).append("</p></div></div>");
				}
			}
		}else {
			sb.append("<div class=text>No messages are available. Once you send message they will appear here.</div>");
		}
		return ResponseEntity.ok(Optional.of(sb.toString()));
		
	}

	public ResponseEntity<Message> insertChat(Integer outgoing, Integer incoming, String message){
		
		Message messages;
		if(message.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		messages = this.messageRepository.save(new Message(incoming, outgoing, message));
		return ResponseEntity.of(Optional.of(messages));
	}

	public String adminChat(int Id, Model model) {
		
		/*User user = this.userRepository.findById(Id).orElse(null);*/
		User user = restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/getUser/"+Id, User.class).getBody();
		if(user==null) return "redirect:/admin/viewUsers/0";
		model.addAttribute("contact", user);
		return "admin/chat";
	}

	public ResponseEntity<Optional<String>> adminGetChat(Integer incoming, HttpSession session){

		User user = (User) session.getAttribute("session_user");

		int outgoing = user.getId();
		StringBuilder sb = new StringBuilder();
		
		List<Message> messages = this.messageRepository.findMessages(outgoing, incoming);
		if(messages.size()>0) {
			for (Message message : messages) {
				if (message.getOutgoing() == outgoing) {
					sb.append("<div class=chat-outgoing><div class=details><p>").append(message.getMsg()).append("</p></div></div>");
				} else {
					sb.append("<div class=chat-incoming><div class=details><p>").append(message.getMsg()).append("</p></div></div>");
				}
			}
		}else {
			sb.append("<div class=text>No messages are available. Once you send message they will appear here.</div>");
		}
		return ResponseEntity.ok(Optional.of(sb.toString()));
		
	}

	public ResponseEntity<Message> adminInsertChat(Integer outgoing, Integer incoming, String message){
		Message messages;
		if(message.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		messages = this.messageRepository.save(new Message(incoming, outgoing, message));
		return ResponseEntity.of(Optional.of(messages));
	}

	public String chatAdmin(Model model) {
		int userId = 1;
		User user = restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/getUser/"+userId, User.class).getBody();
		if(user==null) return "redirect:/user/index";
		model.addAttribute("contact", user);
		model.addAttribute("status", user.isStatus());
		return "admin/chat";
	}
}
