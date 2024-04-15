package com.ridetogether.server.domain.chat.application;

import com.ridetogether.server.domain.chat.dao.RedisRepository;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.Set;

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

    // 1:1 채팅, 그룹 채팅 알람 전송
    public void sendChatAlarm(ChatMessageRequest chatMessageRequest) {
        Set<Long> otherUserIds = chatMessageRequest.getOtherMemberIds();
        Member member = memberRepository.findByIdx(chatMessageRequest.getMemberIdx()).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        otherUserIds.forEach(otherMemberIdx -> messageIfExistsOtherUser(chatMessageRequest, member, otherMemberIdx));
    }

    private void messageIfExistsOtherUser(ChatMessageRequest req, Member member, Long otherMemberIdx) {
        // 채팅방에 받는 사람이 존재하지 않는다면
        if (!redisRepository.existChatRoomUserInfo(otherMemberIdx) || !redisRepository.getUserEnterRoomId(otherMemberIdx).equals(req.getRoomIdx())) {
            Member otherMember = memberRepository.findByIdx(otherMemberIdx).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
            String topic = channelTopic.getTopic();

            // 그룹, 1:1채팅에 따라 제목 변경
            System.out.println(member.getNickName());
            String title = (req.getType() == ChatMessageRequest.MessageType.GROUP_TALK
                    ? req.getRoomTitle() + "에서" : "") + member.getNickName() + "님이 메시지를 보냈습니다.";

//            Alarm alarm = alarmRepository.save(Alarm.builder()
//                    .title(title)
//                    .url("chatURL")
//                    .user(otherUser).build());

//            redisTemplate.convertAndSend(topic, AlarmRequest.toDto(alarm, otherUserId));
        }
    }

}
