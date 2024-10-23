package com.seochang.quiteSeoul.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequestDTO {
    private String loginId;
    private String loginPw;
}
