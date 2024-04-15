package com.ridetogether.server.domain.chat;

import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.security.application.JwtService;
import com.ridetogether.server.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final RedisRepository redisRepository;
    private JwtService jwtService;
    private MemberRepository memberRepository;

    // WebSocket을 통해 들어온 요청이 처리 되기 전에 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String jwtToken = "";

        if (StompCommand.CONNECT == accessor.getCommand()) {
            Member member = SecurityUtil.getLoginMember().orElseThrow(() -> new ErrorHandler(ErrorStatus._UNAUTHORIZED));
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            redisRepository.saveMyInfo(sessionId, member.getIdx());
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");

            // 채팅방에서 나가는 것이 맞는지 확인
            if(redisRepository.existMyInfo(sessionId)) {
                Long userIdx = redisRepository.getMyInfo(sessionId);

                // 채팅방 퇴장 정보 저장
                if(redisRepository.existChatRoomUserInfo(userIdx)) {
                    redisRepository.exitUserEnterRoomId(userIdx);
                }

                redisRepository.deleteMyInfo(sessionId);
            }
        }
        return message;
    }
}
