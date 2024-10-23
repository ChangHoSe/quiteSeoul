package com.seochang.quiteSeoul.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {

    private String loginId;
    private String loginPw;
    private String email;
    private String nickname;

}
