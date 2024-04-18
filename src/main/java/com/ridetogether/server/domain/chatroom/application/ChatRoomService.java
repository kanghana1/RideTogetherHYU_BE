package com.ridetogether.server.domain.chatroom.application;

import com.ridetogether.server.domain.chat.application.RedisSubscriber;
import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.chatroom.converter.ChatRoomDtoConverter;
import com.ridetogether.server.domain.chatroom.dao.ChatRoomRepository;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.chatroom.dto.ChatRoomResponseDto;
import com.ridetogether.server.domain.matching.dao.MatchingRepository;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ridetogether.server.domain.chatroom.dto.ChatRoomResponseDto.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {

    private final MatchingRepository matchingRepository;
    private final ChatRoomRepository chatRoomRepository;
    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, Long, ChatRoom> opsHashChatRoom;
    private HashOperations<String, String, Long> opsHashEnterInfo;
    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        opsHashEnterInfo = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    //채팅방
    //방 아이디로 검색
//    @Cacheable(value = "ChatRoom", cacheManager = "testCacheManager")
    public List<ChatRoom> findAllRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        Collections.reverse(chatRooms);
        log.info(String.valueOf(chatRooms.size()));
        return chatRooms;
//      return  opsHashChatRoom.values(CHAT_ROOMS);
    }

    public ChatRoom findRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
    }

    /**
     * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
     */
    public CreateChatRoomResponseDto createChatRoom(Long chatRoomId, Long memberIdx) {
        Matching matching = matchingRepository.findByIdx(chatRoomId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MATCHING_NOT_FOUND));

        if (chatRoomRepository.existsByChatRoomId(chatRoomId)) {
            throw new ErrorHandler(ErrorStatus.CHAT_ROOM_ALREADY_EXIST);
        } else {
            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomId(chatRoomId)
                    .matching(matching)
                    .userCount(0)
                    .chatStatus(ChatStatus.ACTIVE)
                    .build();
            chatRoomRepository.save(chatRoom);
            enterChatRoom(memberIdx, chatRoomId);
            log.info("채팅방 생성 : {} 번 방", chatRoomId);
            return ChatRoomDtoConverter.convertToCreateChatRoomResponseDto(chatRoom);
        }

    }

    /**
     * 채팅방 입장 : redis에 topic을 만들고 pub/sub 통신을 하기 위해 리스너를 설정한다.
     */
    public void enterChatRoom(Long memberIdx, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId).orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
        String chatRoomIdStr = chatRoomId + "";
        ChannelTopic topic = getTopic(chatRoomIdStr);
        if (topic == null) {
            log.info("기존에 등록된 topic이 없습니다. 새로운 topic 생성 : roomId = {}", chatRoomIdStr);
            topic = new ChannelTopic(chatRoomIdStr);
        }
        redisMessageListener.addMessageListener(redisSubscriber, topic);
        topics.put(chatRoomIdStr, topic);
        plusUserCnt(chatRoomId);

        opsHashChatRoom.put(CHAT_ROOMS, memberIdx, chatRoom);
    }

    public ChannelTopic getTopic(String roomId) {
        log.info("topic 을 불러옵니다 : roomId = {}, topic = {}", roomId, topics.get(roomId));
        return topics.get(roomId);
    }

    public void plusUserCnt(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId).orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
        chatRoom.plusUserCount();
//        chatRoomRepository.save(chatRoom);
    }

    public void minusUserCnt(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId).orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
        if (chatRoom.getUserCount() != 0) {
            chatRoom.minusUserCount();
        }
//        chatRoomRepository.save(chatRoom);
    }

    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setMemberEnterInfo(String sessionId, Long chatRoomId) {
        opsHashEnterInfo.put(ENTER_INFO, sessionId, chatRoomId);
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public Long getMemberEnteredChatRoomId(String sessionId) {
        Long memberIdx = opsHashEnterInfo.get(ENTER_INFO, sessionId);
        if (memberIdx == null) {
            throw new ErrorHandler(ErrorStatus.CHAT_ROOM_MEMBER_NOT_FOUND);
        }
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, memberIdx);
        if (chatRoom == null) {
            throw new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND);
        }
        return chatRoom.getChatRoomId();
    }

    // 사용자가 특정 채팅방에 입장해 있는지 확인
    public boolean existMemberInChatRoom(Long chatRoomId, String sessionId) {
        return getMemberEnteredChatRoomId(sessionId).equals(chatRoomId);
    }

    // 사용자 퇴장
    public void exitMemberEnterChatRoom(Long memberIdx) {
        opsHashEnterInfo.delete(CHAT_ROOMS, memberIdx);
    }

    // 나의 대화상대 정보 저장
    public void saveMyInfo(String sessionId, Long memberIdx) {
        opsHashEnterInfo.put(ENTER_INFO, sessionId, memberIdx);
    }

    public boolean existMyInfo(String sessionId) {
        return opsHashEnterInfo.hasKey(ENTER_INFO, sessionId);
    }

    public Long getMyInfo(String sessionId) {
        return opsHashEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 나의 대화상대 정보 삭제
    public void deleteMyInfo(String sessionId) {
        opsHashEnterInfo.delete(ENTER_INFO, sessionId);
    }

}