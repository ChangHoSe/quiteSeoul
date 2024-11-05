package com.seochang.quiteSeoul.service;

import com.seochang.quiteSeoul.domain.Member;
import com.seochang.quiteSeoul.domain.dto.LoginRequestDTO;
import com.seochang.quiteSeoul.domain.dto.MemberDTO;
import com.seochang.quiteSeoul.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession httpSession;

    public boolean checkEmailDuplicate(String email) {
        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        return memberOptional.isPresent();
    }

    public boolean checkNicknameDuplicate(String nickname) {
        Optional<Member> memberOptional = memberRepository.findByNickname(nickname);
        return memberOptional.isPresent();
    }

    public void registerMember(MemberDTO memberDTO) {
        Member member = new Member();
        member.setNickname(memberDTO.getNickname());
        member.setEmail(memberDTO.getEmail());

        Optional.ofNullable(memberDTO.getLoginId()).ifPresent(member::setLoginId);
        Optional.ofNullable(memberDTO.getLoginPw())
                .map(passwordEncoder::encode)
                .ifPresent(member::setLoginPw);

        memberRepository.save(member);
    }

    //TODO : 세션 시간 제한
    public boolean loginMember(LoginRequestDTO loginRequestDTO) {
        Optional<Member> memberOptional = memberRepository.findByLoginId(loginRequestDTO.getLoginId());

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();

            //중복 로그인 방지
            if (passwordEncoder.matches(loginRequestDTO.getLoginPw(), member.getLoginPw())) {
                if (httpSession.getAttribute("loggedInUser") != null) {
                    httpSession.invalidate();
                }
                httpSession.setAttribute("loggedInUser", member);
                return true;
            }
        }
        return false;
    }

    public void logout() {
        httpSession.invalidate(); //특정 세션을 무력화 하는 방법? 세션 ID를 활용하기 때문
    }

    public String findLoginId(String email) {
        return memberRepository.findByEmail(email)
                .map(Member::getLoginId)
                .orElse(null);
    }

    public void updateLoginPw(String email, String newPassword) {
        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    member.setLoginPw(passwordEncoder.encode(newPassword));
                    memberRepository.save(member);
                });
    }

    public void updateMemberInfo(String email, MemberDTO memberDTO){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원 없음"));

        Optional.ofNullable(memberDTO.getNickname()).ifPresent(member::setNickname);
        Optional.ofNullable(memberDTO.getEmail())
                .ifPresent(newEmail -> {
                    memberRepository.findByEmail(newEmail)
                        .ifPresent(existingMember -> {
                            throw new IllegalArgumentException("이미 존재하는 이메일");
                        });
                    member.setEmail(newEmail);
                });

        memberRepository.save(member);
    }

    //TODO : 회원 탈퇴 -> 탈퇴회원 DB 관리 방법?
     public void deleteMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원 없음"));
        memberRepository.delete(member);
    }

}
