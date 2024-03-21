package com.ridetogether.server.global.util;


import com.ridetogether.server.domain.member.dto.MemberDto.MemberSignupDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.CreateMemberRequestDto;
import com.ridetogether.server.domain.model.Bank;
import com.ridetogether.server.domain.model.Gender;

public class MemberUtil {

	public static MemberSignupDto convertRequestToSignupDto(CreateMemberRequestDto dto) {
		return MemberSignupDto.builder()
				.memberId(dto.getMemberId())
				.password(dto.getPassword())
				.name(dto.getName())
				.email(dto.getEmail())
				.nickName(dto.getNickName())
				.gender(Gender.fromName(dto.getGender()))
				.kakaoPayUrl(dto.getKakaoPayUrl())
				.kakaoQrImageUrl(dto.getKakaoQrImageUrl())
				.account(dto.getAccount())
				.accountBank(Bank.fromName(dto.getAccountBank()))
				.profileImgUrl(dto.getProfileImgUrl())
				.build();
	}
}
