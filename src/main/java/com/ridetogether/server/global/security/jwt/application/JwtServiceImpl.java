package com.ridetogether.server.global.security.jwt.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.security.jwt.JwtToken;
import com.ridetogether.server.global.security.jwt.JwtTokenProvider;
import com.ridetogether.server.global.security.domain.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
@Slf4j
public class JwtServiceImpl implements JwtService {

	private final MemberRepository memberRepository;
	private final ObjectMapper objectMapper;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	@Value("${jwt.access.header}")
	private String accessHeader;
	@Value("${jwt.refresh.header}")
	private String refreshHeader;

	private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	private static final String BEARER = "Bearer ";

	//== 메서드 ==//
	@Transactional
	@Override
	public JwtToken createJwtToken(String username, String password) {
		// 1. username + password 를 기반으로 Authentication 객체 생성
		// 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

		// 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
		// authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// 3. 인증 정보를 기반으로 JWT 토큰 생성
		return jwtTokenProvider.createToken(authentication);
	}

	@Override
	public void updateRefreshToken(String memberId, JwtToken jwtToken) {
		memberRepository.findByMemberId(memberId)
				.ifPresentOrElse(
						member -> member.updateRefreshToken(jwtToken.getRefreshToken()),
						() -> new Exception("회원 조회 실패")
				);
	}

	@Override
	public void destroyRefreshToken(String username) {
		memberRepository.findByMemberId(username)
				.ifPresentOrElse(
						Member::destroyRefreshToken,
						() -> new Exception("회원 조회 실패")
				);
	}

	@Override
	public void sendAccessAndRefreshToken(HttpServletResponse response, JwtToken jwtToken) {
		response.setStatus(HttpServletResponse.SC_OK);

		setAccessTokenHeader(response, jwtToken);
		setRefreshTokenHeader(response, jwtToken);

	}

	@Override
	public void sendAccessToken(HttpServletResponse response, JwtToken jwtToken) {
		response.setStatus(HttpServletResponse.SC_OK);

		setAccessTokenHeader(response, jwtToken);
	}

	@Override
	public Optional<String> extractAccessToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(accessHeader)).filter(
				accessToken -> accessToken.startsWith(BEARER)
		).map(accessToken -> accessToken.replace(BEARER, ""));
	}

	@Override
	public Optional<String> extractRefreshToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(refreshHeader)).filter(
				refreshToken -> refreshToken.startsWith(BEARER)
		).map(refreshToken -> refreshToken.replace(BEARER, ""));
	}

	@Override
	public String extractMemberId(String accessToken) {
		Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		return customUserDetails.getMemberId();
	}

	@Override
	public void setAccessTokenHeader(HttpServletResponse response, JwtToken jwtToken) {
		response.setHeader(accessHeader, jwtToken.getAccessToken());
	}

	@Override
	public void setRefreshTokenHeader(HttpServletResponse response, JwtToken jwtToken) {
		response.setHeader(refreshHeader, jwtToken.getRefreshToken());
	}

	@Override
	public boolean isTokenValid(String token) {
		return jwtTokenProvider.validateToken(token);
	}
}