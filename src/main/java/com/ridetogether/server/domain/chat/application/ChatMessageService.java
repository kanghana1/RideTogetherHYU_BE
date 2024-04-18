package com.ridetogether.server.domain.chat.application;

import com.ridetogether.server.domain.chat.dao.ChatMessageRepository;
import com.ridetogether.server.domain.chat.domain.ChatMessage;
import com.ridetogether.server.domain.chat.dto.ChatMessageDto;
import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.chatroom.dao.ChatRoomRepository;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final RedisRepository redisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;
//    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        topics = new HashMap<>();
    }

    //채팅 전송
    @Transactional
    public void sendMessage(ChatMessageDto chatMessageDto, Member member) {
        ChatRoom chatRoom = chatRoomRepository.findByIdx(chatMessageDto.getRoomIdx())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
        //채팅 생성 및 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(member)
                .message(chatMessageDto.getMessage())
                .chatStatus(ChatStatus.ACTIVE)
                .build();

        chatMessageRepository.save(chatMessage);
        chatRoom.addChatMessage(chatMessage);

        ChannelTopic topic =  new ChannelTopic(chatRoom.getIdx().toString());
        redisMessageListener.addMessageListener(redisSubscriber, topic);

        // ChatMessageRequest 에 유저정보, 현재시간 저장
        chatMessageDto.setNickName(member.getNickName());
        chatMessageDto.setMemberIdx(member.getIdx());

        if (chatMessageDto.getType() == ChatMessageDto.MessageType.GROUP_TALK) {
            // 그륩 채팅일 경우
            redisTemplate.convertAndSend(topic.getTopic(), chatMessageDto);
            redisTemplate.opsForHash();
        } else {
            // 일대일 채팅 이면서 안읽은 메세지 업데이트
            redisTemplate.convertAndSend(topic.getTopic(), chatMessageDto);
            updateUnReadMessageCount(chatMessageDto);
        }
    }

    // 대화 저장
    public void saveMessage(ChatMessageDto chatMessageDto) {
        ChatRoom room = chatRoomRepository.findByRoomId(chatMessageDto.getRoomId());
        // DB 저장
        ChatMessage chatMessage = new ChatMessage(chatMessageDto.getSender(),
                chatMessageDto.getMessage(), chatMessageDto.getS3DataUrl(), room, chatMessageDto.getRoomId(), chatDto.getFileName(), chatDto.getFileDir());
        chatMessageRepository.save(chatMessage);

        // 1. 직렬화
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));

        // 2. redis 저장
        redisTemplate.opsForList().rightPush(chatMessageDto.getRoomIdx(), chatMessageDto);

        // 3. expire 을 이용해서, Key 를 만료시킬 수 있음
        redisTemplate.expire(chatDto.getRoomId(), 1, TimeUnit.MINUTES);
    }
}
