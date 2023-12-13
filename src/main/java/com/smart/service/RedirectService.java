package com.smart.service;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.enums.Role;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Service
public class RedirectService {

    private final Logger logger = LogManager.getLogger(RedirectService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/")
    public String redirect(Principal principal, Model model) {

        User user = userRepository.getUserByEmail(principal.getName()).orElse(null);
        List<Contact> contacts = contactRepository.getContactByEmail(principal.getName());

        if (contacts!=null) {
            for (Contact contact : contacts) contact.setStatus(true);
        }

        assert contacts != null;
        this.contactRepository.saveAll(contacts);

        assert user != null;
        user.setStatus(true);
        this.userRepository.save(user);

//        model.addAttribute("user", user);
//        model.addAttribute("admin", user);

        if(user.getRole().equals(Role.ROLE_ADMIN)) {
            logger.info("ADMIN LOGIN.");
            return "redirect:/admin/index";
        }
        logger.info("USER LOGIN.");
        return "redirect:/user/index";
    }
}
