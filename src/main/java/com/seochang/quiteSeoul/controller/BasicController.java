package com.seochang.quiteSeoul.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicController {

    @GetMapping("/register")
    public String register() {return "register";}

    @GetMapping("/login_form")
    public String loginForm() {return "login_form";}

    @GetMapping("/findId")
    public String findId() {return "findId";}

    @GetMapping("/findPw")
    public String findPw() {return "findPassword";}

}
