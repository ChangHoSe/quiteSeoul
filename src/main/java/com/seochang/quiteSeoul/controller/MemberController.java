package com.seochang.quiteSeoul.controller;

import com.seochang.quiteSeoul.domain.Member;
import com.seochang.quiteSeoul.domain.MemberDetails;
import com.seochang.quiteSeoul.domain.dto.LoginRequestDTO;
import com.seochang.quiteSeoul.domain.dto.MemberDTO;
import com.seochang.quiteSeoul.service.MemberService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/duplicate-check/nickname/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@PathVariable String nickname) {
        boolean isDuplicate = memberService.checkNicknameDuplicate(nickname);
        return ResponseEntity.ok(isDuplicate);
    }

    @GetMapping("/duplicate-check/email/{email}")
    public ResponseEntity<Boolean> checkEmailDuplicate(@PathVariable String email) {
        boolean isDuplicate = memberService.checkEmailDuplicate(email);
        return ResponseEntity.ok(isDuplicate);
    }

    @PostMapping("/register/local")
    public ResponseEntity<Map<String, Object>> localRegister(@RequestBody MemberDTO memberDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            memberService.registerMember(memberDTO);

            response.put("message", "회원가입이 완료되었습니다.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/mypage")
    public String mypage(Model model, @AuthenticationPrincipal MemberDetails member) {
        model.addAttribute("member", member);
        member.getNickname();
        return "mypage";
    }

//    @PostMapping("/login/local")
//    public ResponseEntity<Map<String, Object>> localLogin(@RequestBody LoginRequestDTO loginRequestDTO) {
//        Map<String, Object> response = new HashMap<>();
//        if (memberService.loginMember(loginRequestDTO)) {
//            response.put("message", "로그인 성공");
//            return ResponseEntity.status(HttpStatus.OK).body(response);
//        } else  {
//            response.put("message", "아이디 또는 비밀번호가 잘못되었습니다.");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//        }
//    }

}
