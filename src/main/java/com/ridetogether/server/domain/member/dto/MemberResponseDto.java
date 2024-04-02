package com.ridetogether.server.domain.member.dto;

import lombok.Builder;
import lombok.Data;

public class MemberResponseDto {

	@Data
	@Builder
	public static class SignupResponseDto{
		private Long idx;
		private String nickName;
		private Boolean isSuccess;
	}

	@Data
	@Builder
	public static class MemberInfoResponseDto{
		private Long idx;
		private String memberId;
		private String name;
		private String nickName;
		private String email;
		private String gender;
		private String profileImgUrl;
		private String account;
		private String accountBank;
		private String kakaoPayUrl;
		private String kakaoQrImageUrl;
		private String studentStatus;

	}

	@Data
	@Builder
	public static class ImageResponseDto {
		private String accessUri;
	}



}
