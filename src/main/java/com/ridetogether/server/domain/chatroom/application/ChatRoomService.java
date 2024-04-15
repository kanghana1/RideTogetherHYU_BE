package com.ridetogether.server.domain.chatroom.application;

import com.ridetogether.server.domain.chat.dao.ChatMessageRepository;
import com.ridetogether.server.domain.chat.dao.RedisRepository;
import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.chatroom.dao.ChatRoomMemberRepository;
import com.ridetogether.server.domain.chatroom.dao.ChatRoomRepository;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.chatroom.domain.ChatRoomMember;
import com.ridetogether.server.domain.chatroom.dto.PostOneToOneChatRoomRequest;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisRepository redisRepository;

    @Transactional(rollbackOn = Exception.class)
    public Long createChatRoom(PostOneToOneChatRoomRequest request, Member member) {
        // 1. 상대방 존재 체크
        Member anotherMember = memberRepository.findByIdx(request.getAnotherMemberIdx()).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 2. roomHashCode 만들기
        int roomHashCode = createRoomHashCode(member, anotherMember);

        // 3. 방 존재 확인
        if(existRoom(roomHashCode, member, anotherMember)) {
            ChatRoom savedChatRoom = chatRoomRepository.findByRoomHashCodeAndStatus(roomHashCode, ChatStatus.ACTIVE)
                    .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
            return savedChatRoom.getIdx();
        }

        // 4. 존재하는 방 없다면 생성
        ChatRoom room = ChatRoom.builder()
                .roomHashCode(roomHashCode).build();
        chatRoomRepository.save(room);

        // 5. 해당 유저 채팅 매핑 데이터 생성
        ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                .member(anotherMember)
                .chatRoom(room)
                .name(member.getNickName())
                .status(ChatStatus.ACTIVE)
                .build();
        // 6. 상대방 유저 채팅 매핑 데이터 생성
        ChatRoomMember chatRoomAnotherMember = ChatRoomMember.builder()
                .member(anotherMember)
                .chatRoom(room)
                .name(member.getNickName())
                .status(ChatStatus.ACTIVE)
                .build();

        chatRoomMemberRepository.save(chatRoomMember);
        chatRoomMemberRepository.save(chatRoomAnotherMember);

        return room.getIdx();
    }

    private int createRoomHashCode(Member member, Member anotherMember) {
        Long memberIdx = member.getIdx();
        Long anotherIdx = anotherMember.getIdx();
        return memberIdx > anotherIdx ? Objects.hash(memberIdx, anotherIdx) : Objects.hash(anotherIdx, memberIdx);
    }
}
