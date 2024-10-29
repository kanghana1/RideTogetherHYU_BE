package com.ridetogether.server.global.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.global.security.application.JwtService;
import com.ridetogether.server.global.security.domain.JwtToken;
import com.ridetogether.server.global.security.dto.LoginResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.ridetogether.server.global.security.dto.LoginResponseDto.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String REFRESH_TOKEN = "refreshToken";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String memberId = extractMemberId(authentication);
        JwtToken jwtToken = jwtService.createJwtToken(authentication);

        jwtService.sendAccessToken(response, String.valueOf(jwtToken)); // 이렇게 해도 되는가에 관하여 ..
        log.info( "로그인 성공. memberId: {}" , memberId);
        log.info( "AccessToken 발급. AccessToken: {}" ,jwtToken.getAccessToken());
        log.info( "RefreshToken 발급. RefreshToken: {}" ,jwtToken.getRefreshToken());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        LoginDto loginDto = LoginDto.builder()
                .memberId(memberId)
                .accessToken(jwtToken.getAccessToken())
                .build();

        setCookieForLocal(response, jwtToken);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onSuccess(loginDto)));
    }

    private void setCookieForLocal(HttpServletResponse response, JwtToken jwtToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, jwtToken.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // 모든 곳에서 쿠키 열람 가능
        cookie.setSecure(true); // Https
        cookie.setMaxAge(60 * 60 * 24); // 24시간
        log.info("쿠키 설정 완료");

        response.addCookie(cookie);
    }

    private String extractMemberId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

}
