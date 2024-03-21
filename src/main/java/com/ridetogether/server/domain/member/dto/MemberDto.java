package com.ridetogether.server.domain.member.dto;

import com.ridetogether.server.domain.model.Bank;
import com.ridetogether.server.domain.model.Gender;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
		private Optional<MultipartFile> profileImgUrl;
	}

}
