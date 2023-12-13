package com.smart.configuraton;

import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Component
public class SessionHelper {
    private static final Logger logger = LogManager.getLogger(SessionHelper.class);
    public void removeSessionAttribute() {
        try {
            HttpSession session = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getSession();
            System.out.println(session);
            session.removeAttribute("message");
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
