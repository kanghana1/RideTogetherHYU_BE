package com.ridetogether.server.domain.member.controller;

import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.dto.MemberDto;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberSignupDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.CreateMemberRequestDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.SignupResponseDto;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.global.util.MemberUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<SignupResponseDto> signUp(
			@Valid @RequestBody CreateMemberRequestDto requestDto) throws Exception {
		// request로 들어온 JSON 데이터를 회원가입 Dto로 변환
		MemberSignupDto memberSignupDto = MemberUtil.convertRequestToSignupDto(requestDto);
		Long memberIdx = memberService.save(memberSignupDto);

		SignupResponseDto signupResponseDto = SignupResponseDto.builder()
				.id(memberIdx)
				.nickName(memberSignupDto.getNickName())
				.isSuccess(true).build();
		return ApiResponse.onSuccess(signupResponseDto);
	}
}
