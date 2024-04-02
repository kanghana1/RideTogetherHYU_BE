package com.ridetogether.server.domain.image;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridetogether.server.domain.image.application.OracleImageService;
import com.ridetogether.server.domain.image.dao.ImageRepository;
import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.converter.MemberDtoConverter;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.CreateMemberRequestDto;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.MemberHandler;
import jakarta.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OracleImageServiceTest {

	@Autowired
	OracleImageService oracleFileService;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	ImageRepository imageRepository;


	@Autowired
	MemberService memberService;

	@Autowired
	EntityManager em;

	private static String memberId = "dh1010a";
	private static String PASSWORD = "1234";


	private void clear() {
		em.flush();
		em.clear();
	}

	private CreateMemberRequestDto createMemberRequestDto() {
		return CreateMemberRequestDto.builder()
				.memberId(memberId)
				.password(PASSWORD)
				.name("백도현")
				.email("dh1010a@hanyang.ac.kr")
				.nickName("dh1010a")
				.gender("male")
				.account("100")
				.accountBank("국민은행")
				.build();
	}

	private Long setMember() throws Exception{
		CreateMemberRequestDto memberRequestDto = createMemberRequestDto();
		Long memberId = memberService.signUp(MemberDtoConverter.convertRequestToSignupDto(memberRequestDto));
		clear();
		return memberId;
	}

	// Mock 멀티파트 파일로 하면 File 변환에 문제가 생겨서 File로 테스트 진행
	@Test
	public void 이미지_저장_성공() throws Exception{
//		MockMultipartFile multipartFile = getMultipartFile();
		File body = new File("C:\\Users\\dh101\\Desktop\\img\\git.png");
		Long memberIdx = setMember();
		Member member = memberRepository.findByIdx(memberIdx).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

		String fileDir = memberIdx + "/" + "profile/";
		Long imageIdx = oracleFileService.upload(body, fileDir, member);


		assertThat(imageIdx).isEqualTo(member.getImages().get(0).getIdx());
	}

//	private MockMultipartFile getMultipartFile() throws IOException {
//		File body = new File("C:\\Users\\dh101\\Desktop\\img\\git.png");
//		return new MockMultipartFile("file", "test.png", "img/png",
//				new FileInputStream(body));
//
//	}

	// 임의로 값을 넣어서 하는 테스트
	@Test
	public void 이미지_사전인증요청_모두_삭제() throws Exception{
		oracleFileService.deleteImg("img/test.png");
		oracleFileService.deletePreAuth("KxQNBVfeG0A9TmvDSJReH7dX4K1uLWRIm69jj4b0m6OMbVYgjn1HMUohP4DFOSNq:img/test.png");
	}

}