package com.ridetogether.server.global.security.jwt.application;

import com.ridetogether.server.global.security.jwt.JwtToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface JwtService {

	JwtToken createJwtToken(String memberId, String password);

	void updateRefreshToken(String memberId, JwtToken jwtToken);

	void destroyRefreshToken(String email);

	void sendAccessAndRefreshToken(HttpServletResponse response, JwtToken jwtToken);
	void sendAccessToken(HttpServletResponse response, JwtToken jwtToken);

	Optional<String> extractAccessToken(HttpServletRequest request);

	Optional<String> extractRefreshToken(HttpServletRequest request);

	String extractMemberId(String accessToken);

	void setAccessTokenHeader(HttpServletResponse response, JwtToken jwtToken);

	void setRefreshTokenHeader(HttpServletResponse response, JwtToken jwtToken);

	boolean isTokenValid(String token);

}
