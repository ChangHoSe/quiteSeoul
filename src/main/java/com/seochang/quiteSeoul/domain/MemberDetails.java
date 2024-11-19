package com.seochang.quiteSeoul.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
public class MemberDetails implements UserDetails {

    private final String nickname;
    private final String loginId;
    private final String loginPw;
    private final Collection<? extends GrantedAuthority> authorities;

    public MemberDetails(Member member) {
        this.nickname = member.getNickname();
        this.loginId = member.getLoginId();
        this.loginPw = member.getLoginPw();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(member.getRole()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return loginPw;
    }

    @Override
    public String getUsername() {
        return loginId;
    }
}
