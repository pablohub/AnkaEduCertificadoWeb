package com.pdev.AnkaEduCertificadoWeb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class TestController {

    @Value("${API_USER}")
    private String API_USER;

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/test")
    public ResponseEntity test(){
        logger.info("Test endpoint works succesfully! Usuario: " + API_USER);
        return new ResponseEntity("AnkaEduCertificadoWeb works! Usuario: " + API_USER, HttpStatus.OK);
    }

}
