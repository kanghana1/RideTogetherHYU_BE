package com.ridetogether.server.domain.member.dto;

import com.ridetogether.server.domain.member.model.Bank;
import com.ridetogether.server.domain.member.model.Gender;
import com.ridetogether.server.domain.member.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MemberDto {

	@Builder
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MemberSignupDto {
		private String memberId;
		private String password;
		private String name;
		private String email;
		private String nickName;
		private Gender gender;
		private String kakaoPayUrl;
		private String kakaoQrImageUrl;
		private String account;
		private Bank accountBank;
		private Role role;
	}


}
