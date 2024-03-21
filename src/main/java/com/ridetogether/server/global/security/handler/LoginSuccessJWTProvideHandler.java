package com.ridetogether.server.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.MemberHandler;
import com.ridetogether.server.global.security.dto.LoginResponseDto.LoginDto;
import com.ridetogether.server.global.security.jwt.JwtToken;
import com.ridetogether.server.global.security.jwt.application.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtService jwtService;

	private final MemberRepository memberRepository;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {
		String memberId = extractMemberId(authentication);
		String password = extractPassword(authentication);
		JwtToken jwtToken = jwtService.createJwtToken(memberId, password);

		jwtService.sendAccessAndRefreshToken(response, jwtToken);
		Member member = memberRepository.findByMemberId(memberId)
				.orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
		log.info( "로그인에 성공합니다. nickName: {}" , member.getNickName());
		log.info( "로그인에 성공합니다. email: {}" , member.getEmail());
		log.info( "AccessToken 을 발급합니다. AccessToken: {}" ,jwtToken.getAccessToken());
		log.info( "RefreshToken 을 발급합니다. RefreshToken: {}" ,jwtToken.getRefreshToken());

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");

		LoginDto loginDto = LoginDto.builder()
				.memberId(memberId)
				.accessToken(jwtToken.getAccessToken())
				.refreshToken(jwtToken.getRefreshToken())
				.build();

		response.getWriter().write(objectMapper.writeValueAsString(
				ApiResponse.onSuccess(loginDto)
		));
	}

	private String extractMemberId(Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		return userDetails.getUsername();
	}
	private String extractPassword(Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		return userDetails.getPassword();
	}
}
