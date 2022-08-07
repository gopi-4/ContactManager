package com.smart.controllers;

import java.io.FileInputStream;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helpers.Message;
import com.smart.service.EmailService;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	private String about(Model model) {
	
		model.addAttribute("title", "About");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "SignUp - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "Register";
	}

	@RequestMapping(value = "/do_registration", method = RequestMethod.POST)
	public String do_registration(@Valid @ModelAttribute("user") User user,
			@RequestParam("profileImageUser") MultipartFile file, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {

			if (agreement) {

				if(file.isEmpty()) {
					FileInputStream fileInputStream = new FileInputStream("src/main/resources/static/images/default.jpg");
					user.setImage(Base64Utils.encodeToString(fileInputStream.readAllBytes()));
					fileInputStream.close();
				}else {
					user.setImage(Base64Utils.encodeToString(file.getBytes()));
				}
//				if (file.isEmpty()) {
//					user.setImage("default.jpg");
//					System.out.println("File is empty");
//				} else {
//
//					File saveFile = new ClassPathResource("static"+File.separator+"images").getFile();
//					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + user.getEmail() + file.getOriginalFilename());
//					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//
//					user.setImage(user.getEmail()+file.getOriginalFilename());
//				}

				user.setRole("ROLE_USER");
				user.setEnabled(true);
				user.setStatus(false);
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				user.setCoins(100);

				System.out.println(user);

				if (result.hasErrors()) {
					System.out.println("ERROR:- " + result.toString());
					model.addAttribute("user", user);
					return "Register";
				}

				this.userRepository.save(user);
				
				//check if registering user is already a contact
				Contact contact = this.contactRepository.getContactByEmail(user.getEmail());
				if (contact!=null) {
					contact.setUnique_id(user.getId());
					this.contactRepository.save(contact);
				}

				session.setAttribute("message", new Message("Successfully Registered!! ", "alert-success"));

				model.addAttribute("user", new User());
				model.addAttribute("title", "SignUp - Smart Contact Manager");

				return "Register";
			} else {
				System.out.println("Please Accepts term's and condition's.");
				throw new Exception("You have not agreed term's and condition's.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			model.addAttribute("title", "SignUp - Smart Contact Manager");
			if (e.getMessage().equals(
					"could not execute statement; SQL [n/a]; constraint [user.UK_ob8kqyqqgmefl0aco34akdtpe]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement")) {
				session.setAttribute("message",
						new Message("Something went wrong!! e-mail is already registered.", "alert-danger"));
			} else {
				session.setAttribute("message",
						new Message("Something went wrong!! " + e.getMessage(), "alert-danger"));
			}
			System.out.println(e.getCause());
			return "Register";
		}

	}

	@GetMapping("/signin")
	public String signin(Model model) {
		model.addAttribute("title", "SignIn - Smart Contact Manager");
		return "SignIn";
	}
	
	@GetMapping("/forgetPassWord")
	public String forgetPassWord(Model model) {
		model.addAttribute("title", "Change Password");
		return "forgetPassword";
	}

	@PostMapping("/passwordUpdate")
	public String passwordUpdate(@RequestParam("username") String username, @RequestParam("newPassword") String newPassword,
			HttpSession session, Model model) {
		model.addAttribute("title", "Change Password");
		try {
			User user = this.userRepository.getUserByEmail(username);
			if(user==null) {
				
				session.setAttribute("message", new Message("User not Exist..", "danger"));
				return "forgetPassword";
			}else {
				
				Random random = new Random(1000);
				int OTP = random.nextInt(99999);
				
				String subject = "OTP : Smart Contact Manager";
				String message = "<h1> OTP is <b>"+OTP+"</b></h1>";
				String to = user.getEmail();
				boolean flag = this.emailService.sendEmail(subject, message, to);
				if (flag) {
					session.setAttribute("oldOTP", OTP);
					session.setAttribute("user", user);
					session.setAttribute("newPassword", newPassword);
					model.addAttribute("title", "OTP");
					return "OTP";
				}else {
					session.setAttribute("message", new Message("Error Sending mail..", "danger"));
					return "forgetPassword";
				}
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			session.setAttribute("message", new Message("Error Changing Password..", "danger"));
			return "forgetPassword";
		}		
	}
	
	@PostMapping("OTPVerify")
	public String OTPVerify(@RequestParam("OTP") int UserOTP, HttpSession session, Model model) {
		
		int OTP = (int)session.getAttribute("oldOTP");
		User user = (User)session.getAttribute("user");
		String newPassword = (String)session.getAttribute("newPassword");
		if (OTP == UserOTP) {
			
			model.addAttribute("title", "SignIn - Smart Contact Manager");
			user.setPassword(this.passwordEncoder.encode(newPassword));
			this.userRepository.save(user);
			session.setAttribute("message", new Message("Password Changed Successfully", "success"));
			return "redirect:/signin?change=Password Changed Successfully.";
		}else {
			
			model.addAttribute("title", "OTP");
			session.setAttribute("message", new Message("Wrong OTP!!", "danger"));
			return "OTP";
		}
	}
}