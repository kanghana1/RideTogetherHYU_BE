package com.ridetogether.server.domain.member.controller;

import com.ridetogether.server.domain.image.application.OracleImageService;
import com.ridetogether.server.domain.image.domain.Image;
import com.ridetogether.server.domain.image.model.ImageType;
import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.dto.MemberDto.MemberSignupDto;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.CreateMemberRequestDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.ImageResponseDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.MemberInfoResponseDto;
import com.ridetogether.server.domain.member.dto.MemberResponseDto.SignupResponseDto;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import com.ridetogether.server.domain.member.converter.MemberDtoConverter;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.security.application.JwtService;
import com.ridetogether.server.global.util.SecurityUtil;
import jakarta.validation.Valid;
import java.io.File;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
//@RequestMapping("/api/member")
@Slf4j
public class MemberController {

	private final MemberService memberService;
	private final JwtService jwtService;
	private final OracleImageService oracleImageService;

	@PostMapping("/api/member/signup")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<SignupResponseDto> signUp(
			@Valid @RequestBody CreateMemberRequestDto requestDto) throws Exception {
		// request로 들어온 JSON 데이터를 회원가입 Dto로 변환
		MemberSignupDto memberSignupDto = MemberDtoConverter.convertRequestToSignupDto(requestDto);
		Long memberIdx = memberService.signUp(memberSignupDto);

		SignupResponseDto signupResponseDto = SignupResponseDto.builder()
				.idx(memberIdx)
				.nickName(memberSignupDto.getNickName())
				.isSuccess(true).build();
		return ApiResponse.onSuccess(signupResponseDto);
	}

	@GetMapping("/api/member")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<MemberInfoResponseDto> getMyInfo() {
		MemberInfoResponseDto memberInfoResponseDto = memberService.getMyInfo();
		return ApiResponse.onSuccess(memberInfoResponseDto);
	}

	@PostMapping(value = "/api/member/profile-img")
	public ApiResponse<ImageResponseDto> uploadProfileImage(@RequestPart(value="image", required = true) MultipartFile image) throws Exception{
		Member loginMember = SecurityUtil.getLoginMember()
				.orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		Long imageIdx = oracleImageService.uploadProfileImg(image, loginMember.getIdx());
		String accessUri = oracleImageService.getPublicImgUrl(imageIdx, loginMember.getIdx());

		ImageResponseDto responseDto = ImageResponseDto.builder()
				.accessUri(accessUri)
				.build();

//		File convertFile = new File(System.getProperty("user.home") + "/rideTogetherDummy/" + image.getOriginalFilename());
//		oracleImageService.removeNewFile(convertFile);
		return ApiResponse.onSuccess(responseDto);
	}

	@PostMapping(value = "/api/member/kakao-img")
	public ApiResponse<ImageResponseDto> uploadKakaoImage(@RequestPart(value="image", required = true) MultipartFile image) throws Exception{
		Member loginMember = SecurityUtil.getLoginMember()
				.orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		Long imageIdx = oracleImageService.uploadKakaoQrImg(image, loginMember.getIdx());
		String accessUri = oracleImageService.getPublicImgUrl(imageIdx, loginMember.getIdx());

		ImageResponseDto responseDto = ImageResponseDto.builder()
				.accessUri(accessUri)
				.build();

		return ApiResponse.onSuccess(responseDto);
	}

	@GetMapping("/api/member/kakao-img")
	public ApiResponse<ImageResponseDto> getKakaoImage() throws Exception{
		String accessUri = memberService.getImage(ImageType.KAKAO);
		ImageResponseDto responseDto = ImageResponseDto.builder()
				.accessUri(accessUri)
				.build();
		return ApiResponse.onSuccess(responseDto);
	}

	@GetMapping("/api/member/profile-img")
	public ApiResponse<ImageResponseDto> getProfileImage() throws Exception{
		String accessUri = memberService.getImage(ImageType.PROFILE);
		ImageResponseDto responseDto = ImageResponseDto.builder()
				.accessUri(accessUri)
				.build();
		return ApiResponse.onSuccess(responseDto);
	}

}
