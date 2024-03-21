package com.ridetogether.server.domain.member.application;

import static com.ridetogether.server.global.config.SecurityConfig.passwordEncoder;

import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberSignupDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public Long save(MemberSignupDto userRequestDto) throws Exception {

		Member member = Member.builder()
				.memberId(userRequestDto.getMemberId())
				.password(passwordEncoder().encode(userRequestDto.getPassword()))
				.name(userRequestDto.getName())
				.email(userRequestDto.getEmail())
				.nickName(userRequestDto.getNickName())
				.gender(userRequestDto.getGender())
				.kakaoPayUrl(userRequestDto.getKakaoPayUrl())
				.kakaoQrImageUrl(userRequestDto.getKakaoQrImageUrl())
				.account(userRequestDto.getAccount())
				.accountBank(userRequestDto.getAccountBank())
				.profileImage(userRequestDto.getKakaoQrImageUrl())
				.build();

		return memberRepository.save(member).getIdx();
	}

}
