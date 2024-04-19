package com.ridetogether.server.domain.chat.application;

import com.ridetogether.server.domain.chat.converter.ChatMessageDtoConverter;
import com.ridetogether.server.domain.chat.dao.ChatMessageRepository;
import com.ridetogether.server.domain.chat.domain.ChatMessage;
import com.ridetogether.server.domain.chat.dto.ChatMessageDto;
import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.chatroom.converter.ChatRoomDtoConverter;
import com.ridetogether.server.domain.chatroom.dao.ChatRoomRepository;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final RedisTemplate<Long, ChatMessageDto> redisTemplate;
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
    public ChatMessage createChatMessage(ChatMessageDto chatMessageDto, Long memberIdx) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatMessageDto.getChatRoomId())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
        Member member = memberRepository.findByIdx(memberIdx)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        //채팅 생성 및 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .type(chatMessageDto.getType())
                .senderIdx(member.getIdx())
                .senderNickName(member.getNickName())
                .message(chatMessageDto.getMessage())
                .chatRoom(chatRoom)
                .build();

        chatMessageRepository.save(chatMessage);
        chatRoom.addChatMessage(chatMessage);

        return chatMessage;
    }

    // 대화 저장
    public void saveMessage(ChatMessageDto chatMessageDto) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatMessageDto.getChatRoomId())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
        // DB 저장
        ChatMessage chatMessage = ChatMessage.createChatMessage(chatRoom, chatMessageDto.getSenderIdx(), chatMessageDto.getSenderNickName(),
                chatMessageDto.getMessage(), chatMessageDto.getType());
        chatMessageRepository.save(chatMessage);

        // 1. 직렬화
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));

        // 2. redis 저장
        redisTemplate.opsForList().rightPush(chatMessageDto.getChatRoomId(), chatMessageDto);

        // 3. expire 을 이용해서, Key 를 만료시킬 수 있음
        redisTemplate.expire(chatMessageDto.getChatRoomId() , 1, TimeUnit.MINUTES);
    }

    public List<ChatMessageDto> loadMessage(Long chatRoomId) {
        List<ChatMessageDto> messageList = new ArrayList<>();

        // Redis 에서 해당 채팅방의 메시지 100개 가져오기
        List<ChatMessageDto> redisMessageList = redisTemplate.opsForList().range(chatRoomId , 0, 99);

        // 4. Redis 에서 가져온 메시지가 없다면, DB 에서 메시지 100개 가져오기
        if (redisMessageList == null || redisMessageList.isEmpty()) {
            ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
                    .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
            // 5.
            log.info("db에서 가져오기");
            List<ChatMessage> dbMessageList = chatMessageRepository.findTop100ByChatRoomIdxOrderByCreatedAtDesc(chatRoom.getIdx());

            for (ChatMessage chatMessage : dbMessageList) {
                ChatMessageDto chatDto = ChatMessageDtoConverter.convertChatMessageToDto(chatMessage, chatRoomId);
                messageList.add(chatDto);
                redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));      // 직렬화
                redisTemplate.opsForList().rightPush(chatRoomId, chatDto);                                // redis 저장
            }
        } else {
            // 7.
            messageList.addAll(redisMessageList);
        }

        return messageList;
    }
}
