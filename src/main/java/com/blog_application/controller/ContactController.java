package com.blog_application.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    @GetMapping("/")
    public String getContact(){
        return "Inquiry details are saved to the DB";
    }

}
