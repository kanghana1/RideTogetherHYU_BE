package com.ridetogether.server.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401 인증 실패
		IsLoginSuccessDto isLoginSuccessDto = IsLoginSuccessDto.builder().isSuccess(false).build();
		response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onFailure(ErrorStatus._LOGIN_FAILURE.getCode(),
				ErrorStatus._LOGIN_FAILURE.getMessage(), null)));
		log.info("로그인 실패");
	}

	@Data
	@Builder
	public static class IsLoginSuccessDto {
		private Boolean isSuccess;
	}
}