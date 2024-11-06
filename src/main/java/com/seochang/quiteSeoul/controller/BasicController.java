package com.seochang.quiteSeoul.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicController {

//    @GetMapping("/")
//    public String initial() {
//        return "index";
//    }

    @GetMapping("/register")
    public String register() {return "register";}

//    @GetMapping("/login")
//    public String login() {return "login";}

    @GetMapping("/login_form")
    public String loginForm() {return "login_form";}

    @GetMapping("/findId")
    public String findId() {return "findId";}

    @GetMapping("/findPw")
    public String findPw() {return "findPassword";}

    @GetMapping("/mypage")
    public String mypage() {return "mypage";}

}
