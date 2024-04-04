package com.ridetogether.server.global.login;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.converter.MemberDtoConverter;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.dto.MemberRequestDto.CreateMemberRequestDto;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.code.status.SuccessStatus;
import jakarta.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	MemberService memberService;

	@Autowired
	EntityManager em;

	@Autowired
	private WebApplicationContext webApplicationContext;

	PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	ObjectMapper objectMapper = new ObjectMapper();

	private static String KEY_memberId = "memberId";
	private static String KEY_PASSWORD = "password";
	private static String memberId = "dh1010a";
	private static String PASSWORD = "1234";
	private static String LOGIN_API_URL = "/api/member/login";

	private void clear() {
		em.flush();
		em.clear();
	}


	@BeforeEach
	private void init() throws Exception{
		CreateMemberRequestDto memberSignupDto = CreateMemberRequestDto.builder()
				.memberId(memberId)
				.password(PASSWORD)
				.name("백도현")
				.email("dh1010a@hanyang.ac.kr")
				.nickName("dh1010a")
				.gender("male")
				.kakaoPayUrl("www")
				.account("100")
				.accountBank("국민은행")
				.build();
		memberService.signUp(MemberDtoConverter.convertRequestToSignupDto(memberSignupDto));
	}

	@Test
	public void 로그인_성공() throws Exception{
	    //given
		Map<String, String> map = getMemberIdPasswordMap(memberId, PASSWORD);
	    //when
		MvcResult result = perform(LOGIN_API_URL, APPLICATION_JSON, map)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isSuccess").value(SuccessStatus._OK.getReason().isSuccess()))
				.andExpect(jsonPath("$.code").value(SuccessStatus._OK.getReason().getCode()))
				.andExpect(jsonPath("$.message").value(SuccessStatus._OK.getReason().getMessage()))
				.andReturn();
	}

	@Test
	public void 로그인_아이디_틀림() throws Exception{
		//given
		Map<String, String> map = getMemberIdPasswordMap(memberId+"123", PASSWORD);
		//when
		MvcResult result = perform(LOGIN_API_URL, APPLICATION_JSON, map)
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.isSuccess").value(ErrorStatus.MEMBER_EMAIL_PASSWORD_NOT_MATCH.getReason().isSuccess()))
				.andExpect(jsonPath("$.code").value(ErrorStatus.MEMBER_EMAIL_PASSWORD_NOT_MATCH.getReason().getCode()))
				.andExpect(jsonPath("$.message").value(ErrorStatus.MEMBER_EMAIL_PASSWORD_NOT_MATCH.getReason().getMessage()))
				.andReturn();
	}

	@Test
	public void 로그인_비밀번호_틀림() throws Exception{
		//given
		Map<String, String> map = getMemberIdPasswordMap(memberId, PASSWORD+"123");
		//when
		MvcResult result = perform(LOGIN_API_URL, APPLICATION_JSON, map)
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.isSuccess").value(ErrorStatus.MEMBER_EMAIL_PASSWORD_NOT_MATCH.getReason().isSuccess()))
				.andExpect(jsonPath("$.code").value(ErrorStatus.MEMBER_EMAIL_PASSWORD_NOT_MATCH.getReason().getCode()))
				.andExpect(jsonPath("$.message").value(ErrorStatus.MEMBER_EMAIL_PASSWORD_NOT_MATCH.getReason().getMessage()))
				.andReturn();
	}

	@Test
	public void 로그인_형식_오류시_415() throws Exception{
		//given
		Map<String, String> map = getMemberIdPasswordMap(memberId, PASSWORD);
		//when
		MvcResult result = perform(LOGIN_API_URL, APPLICATION_FORM_URLENCODED, map)
				.andExpect(status().isUnsupportedMediaType())
				.andExpect(jsonPath("$.isSuccess").value(ErrorStatus.MEMBER_LOGIN_NOT_SUPPORT.getReason().isSuccess()))
				.andExpect(jsonPath("$.code").value(ErrorStatus.MEMBER_LOGIN_NOT_SUPPORT.getReason().getCode()))
				.andExpect(jsonPath("$.message").value(ErrorStatus.MEMBER_LOGIN_NOT_SUPPORT.getReason().getMessage()))
				.andReturn();
	}
	@Test
	public void 로그인_메소드_오류시_404() throws Exception{
		//given
		Map<String, String> map = getMemberIdPasswordMap(memberId, PASSWORD);
		//when
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_API_URL)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(map)))
				.andExpect(status().isNotFound())
				.andReturn();
	}

	private ResultActions perform(String url, MediaType mediaType, Map<String, String> usernamePasswordMap) throws Exception {
		return mockMvc.perform(MockMvcRequestBuilders
				.post(url)
				.contentType(mediaType)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(objectMapper.writeValueAsString(usernamePasswordMap)));

	}
	private Map<String, String> getMemberIdPasswordMap(String memberId, String password) {
		Map<String, String> map = new HashMap<>();
		map.put(KEY_memberId, memberId);
		map.put(KEY_PASSWORD, password);
		return map;
	}
}
