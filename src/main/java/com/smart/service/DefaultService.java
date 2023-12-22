package com.smart.service;

import com.smart.dto.Message;
import com.smart.entities.User;
import com.smart.enums.AuthenticationProvider;
import com.smart.enums.Role;
import com.smart.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class DefaultService {

	private final Logger logger = LogManager.getLogger(DefaultService.class);
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private EmailService emailService;
	@Autowired
	private ImageService imageService;

	RestTemplate restTemplate = new RestTemplate();

	public String register(String username, String email, String password, Model model, HttpSession session) {

		try {
			User temp_user = this.userRepository.getUserByEmail(email).orElse(null);
			if (temp_user!=null) throw new Exception("User Exist.");
			User user = new User();
			user.setEmail(email);
			user.setName(username);
			user.setPassword(passwordEncoder.encode(password));
			user.setRole(Role.ROLE_USER);
			user.setEnabled(true);
			user.setStatus(false);
			user.setDate(new Date().toString());
			user.setAuthProvider(AuthenticationProvider.LOCAL);
			user.setImage(imageService.getDefault());

			StaticServices.getApiCall("https://contactmanager-3c3x.onrender.com/contactRegistrationStatus/"+user.getEmail());

			this.userRepository.save(user);

			session.setAttribute("message", new Message("Successfully Registered!! ", "alert-success"));
			model.addAttribute("user", new User());
		}catch (Exception e) {
			session.setAttribute("message", new Message("Something went wrong!! " + e.getMessage(), "alert-danger"));
			logger.error(e.getMessage());
		}
		model.addAttribute("title", "SignUp - Smart Contact Manager");
		return "default/authentication";
	}

	public String passwordUpdate(String username, String newPassword, HttpSession session, Model model) {

		model.addAttribute("title", "Change Password");
		try {
			User user = this.userRepository.getUserByEmail(username).orElse(null);
			if(user==null) {
				session.setAttribute("message", new Message("User not Exist..", "danger"));
				return "new/forgotPassword.html";
			}else {

				int randomPin   =(int) (Math.random()*9000)+1000;

				String subject = "OTP : Smart Contact Manager";
				String message = String.valueOf(randomPin);
				String to = user.getEmail();

				boolean flag = this.emailService.sendEmail(subject, message, to);
				if (flag) {
					session.setAttribute("oldOTP", randomPin);
					session.setAttribute("user", user);
					session.setAttribute("newPassword", newPassword);
					model.addAttribute("title", "OTP");
					return "new/otp.html";
				}else {
					session.setAttribute("message", new Message("Error Sending mail..", "danger"));
					return "new/forgotPassword.html";
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			session.setAttribute("message", new Message("Error Changing Password..", "danger"));
			return "default/forgotPassword.html";
		}
	}

	public String OTPVerification(int UserOTP, HttpSession session, Model model) {
		
		int OTP = (int)session.getAttribute("oldOTP");
		Integer userId = (Integer) session.getAttribute("session_user_Id");
		User user =  restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/getUser/"+userId, User.class).getBody();
		assert user!=null;
		String newPassword = (String)session.getAttribute("newPassword");
		if (OTP == UserOTP) {
			model.addAttribute("title", "SignIn - Smart Contact Manager");
			user.setPassword(this.passwordEncoder.encode(newPassword));
			restTemplate.postForEntity("https://contactmanager-3c3x.onrender.com/updateUser", user, User.class);
			session.setAttribute("message", new Message("Password Changed Successfully", "success"));
			return "redirect:/signIn?change=Password Changed Successfully.";
		}else {
			model.addAttribute("title", "OTP");
			session.setAttribute("message", new Message("Wrong OTP!!", "danger"));
			return "default/otp";
		}
	}
}