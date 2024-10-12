package com.ridetogether.server.global.oauth2.service;

import com.ridetogether.server.global.oauth2.domain.AccessTokenSocialTypeToken;
import com.ridetogether.server.global.oauth2.domain.OAuth2UserDetails;
import com.ridetogether.server.global.oauth2.model.SocialType;
import com.ridetogether.server.global.oauth2.model.info.OAuth2UserInfo;
import com.ridetogether.server.global.oauth2.model.socialLoader.KakaoLoadsStrategy;
import com.ridetogether.server.global.oauth2.model.socialLoader.SocialLoadStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LoadMemberService { // 회원정보 받아오기
    private final RestTemplate restTemplate = new RestTemplate();

    public OAuth2UserDetails getOAuth2UserDetails(AccessTokenSocialTypeToken authentication) {
        SocialType socialType = authentication.getSocialType();
        SocialLoadStrategy socialLoadStrategy = getSocialLoadStrategy(socialType);
        OAuth2UserInfo userInfo = socialLoadStrategy.getUserInfo(authentication.getAccessToken());

        return OAuth2UserDetails.builder() // 빌더 수정 필요
                .socialType(socialType)
                .build();
    }

    private SocialLoadStrategy getSocialLoadStrategy(SocialType socialType) {
        return switch (socialType) {
            case KAKAO -> new KakaoLoadsStrategy();
            default -> throw new IllegalArgumentException("지원하지 않는 로그인 형식입니다");
        };
    }
}
