package com.smart.service;

import com.smart.dto.Message;
import com.smart.entities.EmailVerificationToken;
import com.smart.entities.User;
import com.smart.enums.AuthenticationProvider;
import com.smart.enums.Role;
import com.smart.repository.EmailVerificationTokenRepository;
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
	private EmailVerificationTokenRepository emailVerificationTokenRepository;
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
			user.setName(username.toLowerCase());
			user.setPassword(passwordEncoder.encode(password));
			user.setRole(Role.ROLE_USER);
			user.setEnabled(true);
			user.setStatus(false);
			user.setDate(new Date().toString());
			user.setAuthProvider(AuthenticationProvider.LOCAL);
			user.setImage(imageService.getDefault());

			StaticServices.getApiCall("https://contactmanager-3c3x.onrender.com/contactRegistrationStatus/"+user.getEmail()+"/true");

			this.userRepository.save(user);

			EmailVerificationToken emailVerificationToken = new EmailVerificationToken(user);

			this.emailVerificationTokenRepository.save(emailVerificationToken);

			String subject = "Email Verification";
			String message = String.valueOf("To confirm your account, please click here : "
					+"http://localhost:8080/confirm-account?token="+emailVerificationToken.getToken());
			String to = user.getEmail();

			boolean flag = this.emailService.sendEmail(subject, message, to);

			if(flag) {
				session.setAttribute("message", new Message("Verification Mail Sent!! ", "alert-success"));
			}else {
				session.setAttribute("message", new Message("Sorry!! Try Later.", "alert-danger"));
			}
			model.addAttribute("user", new User());
		}catch (Exception e) {
			session.setAttribute("message", new Message("Something went wrong!! " + e.getMessage(), "alert-danger"));
			logger.error(e.getMessage());
		}
		model.addAttribute("title", "SignUp - Smart Contact Manager");
		return "redirect:/signIn";
	}

	public String generateOTP(String username, String newPassword, HttpSession session, Model model) {

		model.addAttribute("title", "Change Password");
		try {
			User user = this.userRepository.getUserByEmail(username).orElse(null);
			if(user==null) {
				session.setAttribute("message", new Message("User not Exist..", "alert-danger"));
				return "redirect:/forgotPassword";
			}else {

				int randomPin   =(int) (Math.random()*9000)+1000;

				String subject = "OTP : Smart Contact Manager";
				String message = String.valueOf(randomPin);
				String to = user.getEmail();

				boolean flag = this.emailService.sendEmail(subject, message, to);
				if (flag) {
					session.setAttribute("oldOTP", randomPin);
					session.setAttribute("session_user", user);
					session.setAttribute("newPassword", newPassword);
					return "redirect:/verify";
				}else {
					session.setAttribute("message", new Message("Error Sending mail..", "alert-danger"));
					return "redirect:/forgotPassword";
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			session.setAttribute("message", new Message("Error Changing Password..", "alert-danger"));
			return "redirect:/forgotPassword";
		}
	}

	public String verification(int UserOTP, HttpSession session, Model model) {
		
		int OTP = (int)session.getAttribute("oldOTP");
		User user = (User) session.getAttribute("session_user");

		String newPassword = (String)session.getAttribute("newPassword");
		if (OTP == UserOTP) {
			model.addAttribute("title", "SignIn - Smart Contact Manager");
			user.setPassword(this.passwordEncoder.encode(newPassword));
			restTemplate.postForEntity("https://contactmanager-3c3x.onrender.com/updateUser", user, User.class);
			session.setAttribute("message", new Message("Password Changed Successfully", "alert-success"));
			return "redirect:/signIn?change=Password Changed Successfully.";
		}else {
			model.addAttribute("title", "OTP");
			session.setAttribute("message", new Message("Wrong OTP!!", "alert-danger"));
			return "redirect:/verify";
		}
	}

	public String confirmUserAccount(String token, HttpSession session) {
		EmailVerificationToken emailVerificationToken = this.emailVerificationTokenRepository.findByToken(token);
		if(emailVerificationToken==null) {
			session.setAttribute("message", new Message("Token Not Exist..", "alert-danger"));
		}else {
			User user = this.userRepository.getUserByEmail(emailVerificationToken.getUser().getEmail()).orElse(null);
			assert user != null;
			user.setVerified(true);
			this.userRepository.save(user);
			session.setAttribute("message", new Message("Email Verified..", "alert-success"));
		}
		return "redirect:/signIn";
	}
}