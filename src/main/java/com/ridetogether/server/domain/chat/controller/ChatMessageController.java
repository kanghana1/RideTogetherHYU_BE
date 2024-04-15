package com.ridetogether.server.domain.chat.controller;

import com.ridetogether.server.domain.chat.application.ChatMessageService;
import com.ridetogether.server.domain.chat.dto.ChatMessageRequest;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final MemberRepository memberRepository;

    /**
     * websocket "/pub/chat/enter"로 들어오는 메시징을 처리한다.
     * 채팅방에 입장했을 경우
     */
    @MessageMapping("/chat/enter")
    public void enter(
            ChatMessageRequest chatMessageRequest) {
        Member member = memberRepository.findByIdx(chatMessageRequest.getMemberIdx()).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        chatMessageService.enterChatRoom(member.getIdx(), chatMessageRequest.getRoomIdx());
    }

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(
            ChatMessageRequest chatMessageRequest
    ) {
        Member member = memberRepository.findByIdx(chatMessageRequest.getMemberIdx()).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        chatMessageService.sendMessage(chatMessageRequest, member);
    }
}
