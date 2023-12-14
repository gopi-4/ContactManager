package com.smart.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private final Logger logger = LogManager.getLogger(EmailService.class);
	@Autowired
	JavaMailSender javaMailSender;
	public boolean sendEmail(String sub, String mes, String to) {
		boolean f = false;
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			String ADMIN_EMAIL = System.getenv("ADMIN_EMAIL");
	        message.setFrom(ADMIN_EMAIL);
	        message.setTo(to);
	        message.setSubject(sub);
	        message.setText(mes);
	         
	        javaMailSender.send(message);
	         
	        logger.info("Mail successfully sent..");
			f = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return f;
	}
}
