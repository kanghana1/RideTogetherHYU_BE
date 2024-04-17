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

    //안읽은 메세지 업데이트
    private void updateUnReadMessageCount(ChatMessageDto chatMessageDto) {
        Long otherMemberIdx = chatMessageDto.getOtherMemberIds().stream().toList().get(0);
        String roomIdx = String.valueOf(chatMessageDto.getRoomIdx());

        if (!redisRepository.existChatRoomMemberInfo(otherMemberIdx) || !redisRepository.getMemberEnterRoomIdx(otherMemberIdx).equals(chatMessageDto.getRoomIdx())) {

            redisRepository.addChatRoomMessageCount(roomIdx, otherMemberIdx);
            int unReadMessageCount = redisRepository.getChatRoomMessageCount(roomIdx+"", otherMemberIdx);

            String topic = channelTopic.getTopic();

            ChatMessageDto messageRequest = new ChatMessageDto(chatMessageDto, unReadMessageCount);

            redisTemplate.convertAndSend(topic, messageRequest);
        }
    }

    // 1:1 채팅, 그룹 채팅 알람 전송
    public void sendChatAlarm(ChatMessageDto chatMessageDto) {
        Set<Long> otherUserIds = chatMessageDto.getOtherMemberIds();
        Member member = memberRepository.findByIdx(chatMessageDto.getMemberIdx()).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        otherUserIds.forEach(otherMemberIdx -> messageIfExistsOtherUser(chatMessageDto, member, otherMemberIdx));
    }

    private void messageIfExistsOtherUser(ChatMessageDto req, Member member, Long otherMemberIdx) {
        // 채팅방에 받는 사람이 존재하지 않는다면
        if (!redisRepository.existChatRoomMemberInfo(otherMemberIdx) || !redisRepository.getMemberEnterRoomIdx(otherMemberIdx).equals(req.getRoomIdx())) {
            Member otherMember = memberRepository.findByIdx(otherMemberIdx).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
            String topic = channelTopic.getTopic();

            // 그룹, 1:1채팅에 따라 제목 변경
            System.out.println(member.getNickName());
            String title = (req.getType() == ChatMessageDto.MessageType.GROUP_TALK
                    ? req.getRoomTitle() + "에서" : "") + member.getNickName() + "님이 메시지를 보냈습니다.";

//            Alarm alarm = alarmRepository.save(Alarm.builder()
//                    .title(title)
//                    .url("chatURL")
//                    .user(otherUser).build());

//            redisTemplate.convertAndSend(topic, AlarmRequest.toDto(alarm, otherUserId));
        }
    }

}
