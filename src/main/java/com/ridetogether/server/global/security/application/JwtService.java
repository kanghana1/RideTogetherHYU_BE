package com.ridetogether.server.global.security.application;

import com.ridetogether.server.global.security.domain.JwtToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.security.core.Authentication;

public interface JwtService {

	JwtToken createJwtToken(String memberId, String password);
	JwtToken createJwtToken(Authentication authentication);
	String reIssueAccessToken(String memberId, String password);
	String reIssueRefreshToken(String memberId, String password);

	void updateRefreshToken(String memberId, JwtToken jwtToken);

	void destroyRefreshToken(String email);

	void sendAccessAndRefreshToken(HttpServletResponse response, JwtToken jwtToken);
	void sendAccessToken(HttpServletResponse response, String token);

	Optional<String> extractAccessToken(HttpServletRequest request);

	Optional<String> extractRefreshToken(HttpServletRequest request);

	Optional<String> extractMemberId(String accessToken);

	void setAccessTokenHeader(HttpServletResponse response, String token);

	void setRefreshTokenHeader(HttpServletResponse response, String token);

	boolean isTokenValid(String token);

}
