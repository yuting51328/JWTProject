package com.example.resource.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class PageController {

    @GetMapping("/page")
    public String resourcePage() {

        return "ResourcePage";
    }

    @GetMapping("/errorPage")
    public String errorPage() {

        return "errorPage";
    }
}
