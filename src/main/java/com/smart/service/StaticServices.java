package com.smart.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

public class StaticServices {

    private static final Logger logger = LogManager.getLogger(StaticServices.class);

    private static final RestTemplate restTemplate = new RestTemplate();
    @Async("asyncTaskExecutor")
    public static void getApiCall(String url) {
        logger.info(url);
        restTemplate.getForEntity(url, Void.class);
    }
}
