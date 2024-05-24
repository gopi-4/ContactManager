package com.smart.configuraton;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.enums.Role;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class CustomSessionListener implements HttpSessionListener {
    private static final Logger logger = LogManager.getLogger(CustomSessionListener.class);
    private final AtomicInteger counter = new AtomicInteger();
    RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        logger.info("New session is created. Adding Session to the counter.");
        counter.incrementAndGet();  //incrementing the counter
        updateSessionCounter(event);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        try {
            User user = (User) event.getSession().getAttribute("session_user");
            List<Contact> contacts = (List<Contact>) event.getSession().getAttribute("contacts");
            List<User> users = (List<User>) event.getSession().getAttribute("users");
            restTemplate.postForEntity("https://contactmanager-3c3x.onrender.com/logOut", user, User.class);
            if(user.getRole().equals(Role.ROLE_USER)) restTemplate.postForEntity("https://contactmanager-3c3x.onrender.com/saveAllContacts", contacts, List.class);
            else restTemplate.postForEntity("https://contactmanager-3c3x.onrender.com/saveAllUsers", users, List.class);
//            restTemplate.postForEntity("localhost/logOut", user, User.class);
//            if(user.getRole().equals(Role.ROLE_USER)) restTemplate.postForEntity("localhost/saveAllContacts", contacts, List.class);
//            else restTemplate.postForEntity("localhost/saveAllUsers", users, List.class);
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("Session destroyed. Removing the Session from the counter.");
        counter.decrementAndGet();  //decrementing counter
        updateSessionCounter(event);
    }

    private void updateSessionCounter(HttpSessionEvent httpSessionEvent){
        //Let's set in the context
        httpSessionEvent.getSession().getServletContext().setAttribute("activeSession", counter.get());
        logger.info("Total active session are {} ",counter.get());
    }
}