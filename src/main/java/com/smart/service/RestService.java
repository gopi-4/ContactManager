package com.smart.service;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
public class RestService {

    private final Logger logger = LogManager.getLogger(RestService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Async("asyncTaskExecutor")
    public void logout(Integer userId) {
        try {
            User user = this.userRepository.findById(userId).orElse(null);
            assert user != null;
            user.setStatus(false);
            user.setDate(new Date().toString());
            this.userRepository.save(user);
            logger.info(user.getEmail()+" LogOut.");
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Async("asyncTaskExecutor")
    public void updateContactStatusByUserEmail(String email, boolean status) {
        try {
            List<Contact> contacts = contactRepository.getContactByEmail(email);
            if (contacts!=null) {
                for (Contact contact : contacts) contact.setStatus(status);
                this.contactRepository.saveAll(contacts);
            }
            logger.info("Contact status updated to "+status);
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Async("asyncTaskExecutor")
    public void updateRegistrationStatus(String email) {
        try {
            List<Contact> contacts = this.contactRepository.getContactByEmail(email);
            if(contacts!=null) {
                for (Contact contact : contacts) contact.setRegister(true);
                this.contactRepository.saveAll(contacts);
            }
            logger.info("Registration Status Changed Successfully");
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Async("asyncTaskExecutor")
    public static void getApiCall(String url) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForEntity(url, Void.class);
    }
}
