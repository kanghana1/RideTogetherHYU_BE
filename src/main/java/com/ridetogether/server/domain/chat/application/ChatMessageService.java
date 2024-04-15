package com.ridetogether.server.domain.chat.application;

import com.ridetogether.server.domain.chat.RedisRepository;
import com.ridetogether.server.domain.chat.dao.ChatMessageRepository;
import com.ridetogether.server.domain.chat.domain.ChatMessage;
import com.ridetogether.server.domain.chat.dto.ChatMessageRequest;
import com.ridetogether.server.domain.chatroom.dao.ChatRoomRepository;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jvnet.hk2.annotations.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final RedisRepository redisRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
//    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

    // 채팅방 입장
    public void enterChatRoom(Long memberIdx, Long chatRoomIdx) {
        // 그룹채팅은 해시코드가 존재 x, 일대일 채팅은 해시코드가 존재.
        ChatRoom chatRoom = chatRoomRepository.findByIdx(chatRoomIdx)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
        redisRepository.userEnterRoomInfo(memberIdx, chatRoomIdx);
        if (chatRoom.getRoomHashCode() != 0) {
            redisRepository.initChatRoomMessageInfo(chatRoomIdx+"", memberIdx);
        }
    }

    //채팅 전송
    @Transactional
    public void sendMessage(ChatMessageRequest chatMessageRequest, Member member) {
        ChatRoom chatRoom = chatRoomRepository.findByIdx(chatMessageRequest.getRoomIdx())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
        //채팅 생성 및 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(member)
                .message(chatMessageRequest.getMessage())
                .build();

        chatMessageRepository.save(chatMessage);
        String topic = channelTopic.getTopic();

        // ChatMessageRequest에 유저정보, 현재시간 저장
        chatMessageRequest.setNickName(member.getNickName());
        chatMessageRequest.setMemberIdx(member.getIdx());

        if (chatMessageRequest.getType() == ChatMessageRequest.MessageType.GROUP_TALK) {
            // 그륩 채팅일 경우
            redisTemplate.convertAndSend(topic, chatMessageRequest);
            redisTemplate.opsForHash();
        } else {
            // 일대일 채팅 이면서 안읽은 메세지 업데이트
            redisTemplate.convertAndSend(topic, chatMessageRequest);
            updateUnReadMessageCount(chatMessageRequest);
        }
    }

    //안읽은 메세지 업데이트
    private void updateUnReadMessageCount(ChatMessageRequest chatMessageRequest) {
        Long otherMemberIdx = chatMessageRequest.getOtherMemberIds().stream().toList().get(0);
        String roomIdx = String.valueOf(chatMessageRequest.getRoomIdx());

        if (!redisRepository.existChatRoomUserInfo(otherMemberIdx) || !redisRepository.getUserEnterRoomId(otherMemberIdx).equals(chatMessageRequest.getRoomIdx())) {

            redisRepository.addChatRoomMessageCount(roomIdx, otherMemberIdx);
            int unReadMessageCount = redisRepository.getChatRoomMessageCount(roomIdx+"", otherMemberIdx);

            String topic = channelTopic.getTopic();

            ChatMessageRequest messageRequest = new ChatMessageRequest(chatMessageRequest, unReadMessageCount);

            redisTemplate.convertAndSend(topic, messageRequest);
        }
    }

}
