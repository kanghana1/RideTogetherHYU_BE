package com.ridetogether.server.domain.mail.controller;


import com.ridetogether.server.domain.mail.dto.MailDto.CheckMailRequestDto;
import com.ridetogether.server.domain.mail.dto.MailResponseDto;
import com.ridetogether.server.domain.mail.dto.MailResponseDto.CheckMailResponseDto;
import com.ridetogether.server.domain.mail.application.MailService;
import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
//@RequestMapping("/api/email")
@RestController
public class MailController {

    private final MailService mailService;
    private final MemberService memberService;


    @PostMapping("/api/email/send")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MailResponseDto.SendMailResponseDto> sendMail(@RequestParam(value = "email") String email) {
        return ApiResponse.onSuccess(mailService.sendEmail(email));
    }

    @PostMapping("/api/email/confirm")
    public ApiResponse<CheckMailResponseDto> confirmEmail(@RequestBody @Valid CheckMailRequestDto dto) {
        return ApiResponse.onSuccess(mailService.checkEmail(dto.getEmail(), dto.getAuthNumber()));
    }


}
