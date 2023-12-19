package com.smart.service;

import com.smart.entities.User;
import com.smart.enums.Role;
import com.smart.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;

@Service
public class RedirectService {

    private final Logger logger = LogManager.getLogger(RedirectService.class);
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String redirect(Principal principal, Model model, HttpSession session) {

        User user = userRepository.getUserByEmail(principal.getName()).orElse(null);

        RestTemplate restTemplate = new RestTemplate();
        assert user != null;
        ResponseEntity<Void> responseEntity = restTemplate.getForEntity("https://contactmanager-3c3x.onrender.com/updateContactStatus/"+user.getEmail()+"/true", Void.class);

        user.setStatus(true);
        this.userRepository.save(user);

        session.setAttribute("session_user", user);

        if(user.getRole().equals(Role.ROLE_ADMIN)) {
            logger.info("ADMIN LOGIN.");
            return "redirect:/admin/index";
        }
        logger.info("USER LOGIN.");
        return "redirect:/user/index";
    }
}
