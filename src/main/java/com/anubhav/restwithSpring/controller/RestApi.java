package com.anubhav.restwithSpring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RestApi {

    private static final Logger logger = LoggerFactory.getLogger(RestApi.class);

    @RequestMapping("/hello")
    @ResponseBody
    public String getHealthCheck() throws InterruptedException {
        logger.info("**********going to return the response******");
        return "**********going to return the response******";
    }
}
