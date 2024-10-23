package com.seochang.quiteSeoul.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicController {

    @GetMapping("/")
    public String initial() {
        return "index";
    }

    @GetMapping("/register")
    public String register() {return "register";}


}
