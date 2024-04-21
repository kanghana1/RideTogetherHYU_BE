package com.ridetogether.server.domain.chat.application;

import com.ridetogether.server.domain.chatroom.application.ChatRoomService;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.security.application.JwtService;
import com.ridetogether.server.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {
    ChatRoomService chatRoomService;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    // WebSocket을 통해 들어온 요청이 처리 되기 전에 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String jwtToken = "";

        if (StompCommand.CONNECT == accessor.getCommand()) {
            Member member = SecurityUtil.getLoginMember().orElseThrow(() -> new ErrorHandler(ErrorStatus._UNAUTHORIZED));
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            chatRoomService.saveMyInfo(sessionId, member.getIdx());

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");

            // 채팅방에서 나가는 것이 맞는지 확인
            if(chatRoomService.existMyInfo(sessionId)) {
                Long memberIdx = chatRoomService.getMyInfo(sessionId);
                Long chatRoomId = chatRoomService.getMemberEnteredChatRoomId(sessionId);
                if (chatRoomId == null) {
                    log.error("Stomp Handler : 채팅방을 찾는데 실패하였습니다. memberIdx : {}", memberIdx);
                    throw new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND);
                }

                // 채팅방 퇴장 정보 저장
                if(chatRoomService.existMemberInChatRoom(chatRoomId, sessionId)) {
                    chatRoomService.exitMemberEnterChatRoom(memberIdx);
                }

                chatRoomService.deleteMyInfo(sessionId);
                chatRoomService.minusUserCnt(chatRoomId);
            }
        }
        return message;
    }
}
