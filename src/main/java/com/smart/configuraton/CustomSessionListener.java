package com.smart.configuraton;

import com.smart.entities.User;
import com.smart.service.RestService;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class CustomSessionListener implements HttpSessionListener {
    private static final Logger logger = LogManager.getLogger(CustomSessionListener.class);
    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public void sessionCreated(HttpSessionEvent event) {

        logger.info("New session is created. Adding Session to the counter.");
        counter.incrementAndGet();  //incrementing the counter
        updateSessionCounter(event);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        try {
            logger.info("Session destroyed. Removing the Session from the counter.");
            counter.decrementAndGet();  //decrementing counter
            User user = (User) event.getSession().getAttribute("session_user");
            RestService.getApiCall("https://contactmanager-3c3x.onrender.com/logOut/"+user.getId());
            RestService.getApiCall("https://contactmanager-3c3x.onrender.com/updateContactStatus/"+user.getEmail()+"/false");
            updateSessionCounter(event);
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void updateSessionCounter(HttpSessionEvent httpSessionEvent){
        //Let's set in the context
        httpSessionEvent.getSession().getServletContext().setAttribute("activeSession", counter.get());
        logger.info("Total active session are {} ",counter.get());
    }
}