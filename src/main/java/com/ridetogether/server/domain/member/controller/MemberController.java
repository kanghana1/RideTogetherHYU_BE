package com.ridetogether.server.domain.member.controller;

import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberSignupDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.CreateMemberRequestDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.LoginMemberRequestDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.SignupResponseDto;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.global.converter.MemberDtoConverter;
import com.ridetogether.server.global.security.jwt.JwtToken;
import com.ridetogether.server.global.security.jwt.application.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
@Slf4j
public class MemberController {

	private final MemberService memberService;
	private final JwtService jwtService;

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<SignupResponseDto> signUp(
			@Valid @RequestBody CreateMemberRequestDto requestDto) throws Exception {
		// request로 들어온 JSON 데이터를 회원가입 Dto로 변환
		MemberSignupDto memberSignupDto = MemberDtoConverter.convertRequestToSignupDto(requestDto);
		Long memberIdx = memberService.singUp(memberSignupDto);

		SignupResponseDto signupResponseDto = SignupResponseDto.builder()
				.idx(memberIdx)
				.nickName(memberSignupDto.getNickName())
				.isSuccess(true).build();
		return ApiResponse.onSuccess(signupResponseDto);
	}

//	@PostMapping("/login")
//	@ResponseStatus(HttpStatus.OK)
//	public ApiResponse<JwtToken> login(@Valid @RequestBody LoginMemberRequestDto requestDto, BindingResult bindingResult) {
//		JwtToken jwtToken = memberService.login(requestDto);
//		log.info( "로그인에 성공합니다. MemberId: {}" , requestDto.getMemberId());
//		log.info( "AccessToken 을 발급합니다. AccessToken: {}" ,jwtToken.getAccessToken());
//		log.info( "RefreshToken 을 발급합니다. RefreshToken: {}" ,jwtToken.getRefreshToken());
//		return ApiResponse.onSuccess(jwtToken);
//	}
}
