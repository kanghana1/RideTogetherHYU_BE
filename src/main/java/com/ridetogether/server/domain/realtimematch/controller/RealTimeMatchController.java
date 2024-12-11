package com.ridetogether.server.domain.realtimematch.controller;

import com.ridetogether.server.domain.member.application.MemberService;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchRequestDto;
import com.ridetogether.server.domain.realtimematch.service.RealTimeMatchService;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import static com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchRequestDto.*;

@RequiredArgsConstructor
@Controller
@Slf4j
public class RealTimeMatchController {

    // 매칭은 다대일 구조라서 pub/sub 구조를 사용하는 게 적합해보임

    private final SimpMessageSendingOperations messagingTemplate;
    private final RealTimeMatchService realTimeMatchService;
    private final MemberService memberService;



    @MessageMapping("/match/join")
    public void joinMatch(@Payload EnterAndLeaveMatchRequest request) {
        String memberId = SecurityUtil.getLoginMemberId().orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Member loginMember = memberService.findByMemberId(memberId);

        realTimeMatchService.enterMatching(request);


    }




}
