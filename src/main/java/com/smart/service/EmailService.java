package com.smart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	JavaMailSender javaMailSender;
	
	public boolean sendEmail(String sub, String mes, String to) {
		
        
		boolean f = false;
		
//		String from = "gkalyankar33@gmail.com";
//		String password = "gkalyankar33@gmail";
//		
//		Properties properties = System.getProperties();
//		
//		properties.put("mail.smtp.host", "smtp.gmail.com");
//		properties.put("mail.smtp.port", 465);
//		properties.put("mail.smtp.ssl.enable", "true");
//		properties.put("mail.smtp.auth", "true");
//		properties.put("mail.smtp.starttls.enable", "true");
		
//		Session session = Session.getInstance(properties, new Authenticator() {
//
//			@Override
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication(from, password);
//			}
//		
//		});
//		
//		session.setDebug(true);
//		
//		MimeMessage m = new MimeMessage(session);
		
		try {
//			m.setFrom(new InternetAddress(from));
//			
//			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//			
//			m.setSubject(subject);
//			
////			m.setText(message);
//			m.setContent(message, "text/html");
//			
//			Transport.send(m);
//			
//			System.out.println("Send successfully....");
			
			SimpleMailMessage message = new SimpleMailMessage();
	        
	        message.setFrom("gkalyankar33@gmail.com");
	        message.setTo(to);
	        message.setSubject(sub);
	        message.setText(mes);
	         
	        javaMailSender.send(message);
	         
	        System.out.println("Mail successfully sent..");
	        
			f = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return f;
	}
}
