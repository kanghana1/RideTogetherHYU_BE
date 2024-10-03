package com.ridetogether.server.global.oauth2.domain;

import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.global.oauth2.service.LoadMemberService;
import com.ridetogether.server.global.security.domain.CustomUserDetails;
import com.ridetogether.server.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

    private final LoadMemberService loadMemberService;
    private final MemberRepository memberRepository;

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        CustomUserDetails OAuth2User = loadMemberService.getOAuth2UserDetails();

        Member member = saveOrGet(OAuth2User);

        return null;

    }

    private Member saveOrGet(CustomUserDetails oAuth2User) {
        /*Optional<Member> member = memberRepository.findBySocialTypeAndMemberId(); // 이게 필요할까 ?
        if (member.isEmpty()) {
            // 고민해보기
        }
        return member.get();*/

        return null;
    }

    // 구현 고민하기 (Socia lType 때문)
    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
