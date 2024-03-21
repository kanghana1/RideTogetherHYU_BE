package com.ridetogether.server.domain.member.dto;

import lombok.Builder;
import lombok.Data;

public class MemberResponseDto {

	@Data
	@Builder
	public static class SignupResponseDto{
		private Long id;
		private String nickName;
		private Boolean isSuccess;
	}

}
