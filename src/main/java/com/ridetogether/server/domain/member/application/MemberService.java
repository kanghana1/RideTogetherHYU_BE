package com.ridetogether.server.domain.member.application;

import static com.ridetogether.server.global.config.SecurityConfig.passwordEncoder;

import com.ridetogether.server.domain.member.converter.MemberDtoConverter;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberSignupDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.MemberInfoResponseDto;
import com.ridetogether.server.domain.member.model.ActiveState;
import com.ridetogether.server.domain.member.model.StudentStatus;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.security.application.JwtService;
import com.ridetogether.server.global.util.SecurityUtil;
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

	private static final String HANYANG_EMAIL = "@hanyang.ac.kr";

	public Long signUp(MemberSignupDto memberSignupDto) throws Exception {
		if (isExistByEmail(memberSignupDto.getEmail())) {
			throw new ErrorHandler(ErrorStatus.MEMBER_EMAIL_ALREADY_EXIST);
		}
		if (isExistByNickName(memberSignupDto.getNickName())) {
			throw new ErrorHandler(ErrorStatus.MEMBER_NICKNAME_ALREADY_EXIST);
		}
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
				.activeState(ActiveState.ACTIVE)
				.studentStatus(StudentStatus.NOT_STUDENT)
				.build();

		member.setStudentStatus(member.getMemberId());

		return memberRepository.save(member).getIdx();
	}

	public MemberInfoResponseDto getMyInfo() {
		Member member = SecurityUtil.getLoginMember().orElseThrow(() -> new ErrorHandler(ErrorStatus._UNAUTHORIZED));
		return MemberDtoConverter.convertMemberToInfoResponseDto(member);
	}

	public boolean isExistByEmail(String email) {
		return memberRepository.existsByEmail(email);
	}

	public boolean isExistByNickName(String nickName) {
		return memberRepository.existsByNickName(nickName);
	}

	public void createException() {
		throw new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND);
	}
}
