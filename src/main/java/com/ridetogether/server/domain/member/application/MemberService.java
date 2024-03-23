package com.ridetogether.server.domain.member.application;

import static com.ridetogether.server.global.config.SecurityConfig.passwordEncoder;

import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberSignupDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.LoginMemberRequestDto;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.MemberHandler;
import com.ridetogether.server.global.security.jwt.JwtToken;
import com.ridetogether.server.global.security.jwt.application.JwtService;
import java.util.Optional;
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
	private final JwtService jwtService;

	public Long singUp(MemberSignupDto memberSignupDto) throws Exception {

		Member member = Member.builder()
				.memberId(memberSignupDto.getMemberId())
				.password(passwordEncoder().encode(memberSignupDto.getPassword()))
				.name(memberSignupDto.getName())
				.email(memberSignupDto.getEmail())
				.nickName(memberSignupDto.getNickName())
				.gender(memberSignupDto.getGender())
				.kakaoPayUrl(memberSignupDto.getKakaoPayUrl())
				.kakaoQrImageUrl(memberSignupDto.getKakaoQrImageUrl())
				.account(memberSignupDto.getAccount())
				.accountBank(memberSignupDto.getAccountBank())
				.role(memberSignupDto.getRole())
				.build();

		return memberRepository.save(member).getIdx();
	}

//	public JwtToken login(LoginMemberRequestDto requestDto) {
//		Member member = memberRepository.findByMemberId(requestDto.getMemberId())
//				.orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
//		return jwtService.createJwtToken(requestDto.getMemberId(), requestDto.getPassword());
//	}

}
