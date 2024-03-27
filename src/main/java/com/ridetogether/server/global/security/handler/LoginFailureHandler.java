package com.ridetogether.server.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setStatus(ErrorStatus.MEMBER_EMAIL_PASSWORD_NOT_MATCH.getHttpStatus().value());
		response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onFailure(ErrorStatus.MEMBER_EMAIL_PASSWORD_NOT_MATCH.getCode(),
				ErrorStatus.MEMBER_EMAIL_PASSWORD_NOT_MATCH.getMessage(), exception.getMessage())));
		log.info("Authentication failed: " + exception.getMessage());
	}
}