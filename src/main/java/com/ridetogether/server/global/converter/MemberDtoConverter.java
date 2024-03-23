package com.ridetogether.server.global.converter;


import com.ridetogether.server.domain.member.dto.MemberDto.MemberSignupDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.CreateMemberRequestDto;
import com.ridetogether.server.domain.model.Bank;
import com.ridetogether.server.domain.model.Gender;
import com.ridetogether.server.domain.model.Role;

public class MemberDtoConverter {

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
				.role(Role.ADMIN)
				.build();
	}
}
