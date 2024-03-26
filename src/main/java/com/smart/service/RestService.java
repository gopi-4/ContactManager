package com.smart.service;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
    public void logout(User user) {
        try {
            user.setStatus(false);
            user.setDate(new Date().toString());
            updateUser(user);
            logger.info(user.getEmail()+" LogOut.");
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Async("asyncTaskExecutor")
    public void updateRegistrationStatus(String email, boolean status) {
        try {
            List<Contact> contacts = this.contactRepository.getContactByEmail(email);
            if(contacts!=null) {
                for (Contact contact : contacts) {
                    contact.setRegister(status);
                    updateContact(contact);
                }
            }
            logger.info("Registration Status of Contact's Changed Successfully for "+email.toUpperCase());
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Cacheable(cacheNames = "users",key="#Id")
    public User getUser(int Id) {
        return this.userRepository.findById(Id).orElse(null);
    }

    @CachePut(cacheNames = "users", key = "#user.Id")
    public User updateUser(User user) {
        return this.userRepository.save(user);
    }

    @Async("asyncTaskExecutor")
    @CacheEvict(cacheNames = "users", key = "#user.Id", allEntries = true, beforeInvocation = true)
    public void deleteUser(int Id) {
        this.userRepository.deleteById(Id);
    }

    @Cacheable(cacheNames = "contacts",key = "#Id")
    public Contact getContactById(int Id) {
        return this.contactRepository.findById(Id).orElse(null);
    }

    @CachePut(cacheNames = "contacts",key = "#contact.Id")
    public Contact updateContact(Contact contact) {
        return this.contactRepository.save(contact);
    }

    @Async("asyncTaskExecutor")
    @CacheEvict(cacheNames = "contacts", key = "#Id", allEntries = true, beforeInvocation = true)
    public void deleteContactById(int Id) {
        this.contactRepository.deleteById(Id);
    }

    @Async("asyncTaskExecutor")
    public void saveAllContacts(List<Contact> contacts) {
        this.contactRepository.saveAll(contacts);
    }

    @Async("asyncTaskExecutor")
    public void saveAllUsers(List<User> users) {
        this.userRepository.saveAll(users);
    }
}
