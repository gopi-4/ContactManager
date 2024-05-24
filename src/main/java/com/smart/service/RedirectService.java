package com.smart.service;

import com.smart.dto.Message;
import com.smart.entities.User;
import com.smart.enums.Role;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;

@Service
public class RedirectService {

    private final Logger logger = LogManager.getLogger(RedirectService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    public String redirect(Principal principal, Model model, HttpSession session) {

        User user = this.userRepository.getUserByEmail(principal.getName()).orElse(null);

        assert user != null;
        if(!user.isVerified()) {
            session.setAttribute("message", new Message("Please Verify Email..", "alert-danger"));
            return "redirect:/signIn";
        }
        user.setStatus(true);
        session.setAttribute("session_user", user);

        if(user.getRole().equals(Role.ROLE_ADMIN)) {
            session.setAttribute("users", this.userRepository.findByRole(Role.ROLE_USER));
            logger.info("ADMIN LOGIN.");
            return "redirect:/admin/index";
        }
        session.setAttribute("contacts", this.contactRepository.findContactsByUserId(user.getId()));
        logger.info("USER LOGIN.");
        return "redirect:/user/index";
    }
}
