package com.ridetogether.server.domain.chat.handler;

import com.ridetogether.server.domain.chatroom.dao.RedisRepository;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import com.ridetogether.server.global.security.application.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {
//    private final ChatRoomService chatRoomService;
    private final RedisRepository redisRepository;
    private final JwtService jwtService;
//    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    // WebSocket을 통해 들어온 요청이 처리 되기 전에 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            Optional<String> accessToken = Optional.ofNullable(String.valueOf(headerAccessor.getNativeHeader("Authorization").get(0)))
                    .filter(token -> token.startsWith("Bearer "))
                    .map(token -> token.replace("Bearer ", ""));

            if (accessToken.isEmpty()) {
                log.error("Stomp Handler : 유효하지 않은 토큰입니다.");
                throw new MessageDeliveryException(ErrorStatus._UNAUTHORIZED.getMessage());
            }

            String memberId = jwtService.extractMemberId(accessToken.get()).orElse(null);

            log.info("Stomp Handler : CONNECTED. memberId : {}", memberId);

            if (!jwtService.isTokenValid(accessToken.get())) {
                log.error("Stomp Handler : 유효하지 않은 토큰입니다. memberId : {}", memberId);
                throw new MessageDeliveryException(ErrorStatus._UNAUTHORIZED.getMessage());
            }


            Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            redisRepository.saveMyInfo(sessionId, member.getIdx());

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");

            // 채팅방에서 나가는 것이 맞는지 확인
            if(redisRepository.existMyInfo(sessionId)) {
                Long memberIdx = redisRepository.getMyInfo(sessionId);
                Long chatRoomId = redisRepository.getMemberEnteredChatRoomId(sessionId);
                log.info("Exit chatroom. memberIdx : {}, chatRoomId : {}", memberIdx, chatRoomId);
                if (chatRoomId == null) {
                    log.error("Stomp Handler : 채팅방을 찾는데 실패하였습니다. memberIdx : {}", memberIdx);
                    throw new MessageDeliveryException(ErrorStatus.CHAT_ROOM_NOT_FOUND.getMessage());
                }

                // 채팅방 퇴장 정보 저장
                if(redisRepository.existMemberInChatRoom(chatRoomId, sessionId)) {
                    redisRepository.exitMemberEnterChatRoom(memberIdx);
                }

                redisRepository.deleteMyInfo(sessionId);
                redisRepository.minusUserCnt(chatRoomId);
            }
        }
        return message;
    }
}
