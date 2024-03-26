package com.smart.service;

import com.smart.entities.Contact;
import com.smart.entities.User;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class StaticServices {
    private static final Logger logger = LogManager.getLogger(StaticServices.class);
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final int PAGE_SIZE = 5;

    @Async("asyncTaskExecutor")
    public static void getApiCall(String url) {
        logger.info("Get API Call.");
        restTemplate.getForEntity(url, Void.class);
    }

    public static Page<Contact> getContacts(int page, HttpSession session, List<Integer> online_users) {

        Pageable pageRequest = PageRequest.of(page, PAGE_SIZE);

        List<Contact> contacts = (List<Contact>) session.getAttribute("contacts");

        int index=0;
        for(Contact contact : contacts) {
            contact.setIndex(index);
            index++;
            contact.setStatus(online_users.contains(contact.hashCode()));
        }

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), contacts.size());

        List<Contact> pageContent = contacts.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, contacts.size());
    }

    public static Page<User> getUsers(int page, HttpSession session, List<Integer> online_users) {


        Pageable pageRequest = PageRequest.of(page, PAGE_SIZE);

        List<User> users = (List<User>) session.getAttribute("users");

        int index=0;
        for(User user : users) {
            user.setIndex(index);
            index++;
            user.setStatus(online_users.contains(user.hashCode()));
        }

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), users.size());

        List<User> pageContent = users.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, users.size());
    }
}
