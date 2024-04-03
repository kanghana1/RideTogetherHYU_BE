package com.ridetogether.server.domain.member.application;

import static com.ridetogether.server.global.config.SecurityConfig.passwordEncoder;

import com.ridetogether.server.domain.image.domain.Image;
import com.ridetogether.server.domain.image.dto.ImageDto.ImageUriResponseDto;
import com.ridetogether.server.domain.image.model.ImageType;
import com.ridetogether.server.domain.member.converter.MemberDtoConverter;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberSignupDto;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberUpdateDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.MemberInfoResponseDto;
import com.ridetogether.server.domain.member.model.ActiveState;
import com.ridetogether.server.domain.member.model.StudentStatus;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.security.application.JwtService;
import com.ridetogether.server.global.util.SecurityUtil;
import java.util.List;
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

	public ImageUriResponseDto getImage(ImageType imageType, Long idx) {
		Member member = memberRepository.findByIdx(idx)
				.orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		List<Image> images = member.getImages();
		if (images.isEmpty()) {
			throw new ErrorHandler(ErrorStatus.IMAGE_NOT_FOUND);
		}
		for (Image x : images) {
			if (x.getImageType() == imageType) {
				return ImageUriResponseDto.builder()
						.accessUri(x.getAccessUri())
						.build();
			}
		}
		return null;
	}

	public MemberInfoResponseDto updateMember(MemberUpdateDto updateDto) {
		Member member = memberRepository.findByMemberId(updateDto.getMemberId())
				.orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		member.updateMember(updateDto);
		return MemberDtoConverter.convertMemberToInfoResponseDto(member);
	}

	public boolean isExistByEmail(String email) {
		return memberRepository.existsByEmail(email);
	}

	public boolean isExistByNickName(String nickName) {
		return memberRepository.existsByNickName(nickName);
	}

	public boolean isExistByMemberId(String memberId) {
		return memberRepository.existsByMemberId(memberId);
	}

}
