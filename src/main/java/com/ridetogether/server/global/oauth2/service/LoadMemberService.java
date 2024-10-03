package com.ridetogether.server.global.oauth2.service;

import com.ridetogether.server.global.security.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LoadMemberService { // 회원정보 받아오기
    private final RestTemplate restTemplate = new RestTemplate();


    public CustomUserDetails getOAuth2UserDetails() {
        return null;
    }
}
