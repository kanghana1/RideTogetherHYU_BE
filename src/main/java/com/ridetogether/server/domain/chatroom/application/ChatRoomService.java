package com.ridetogether.server.domain.chatroom.application;

import com.ridetogether.server.domain.chat.dao.ChatMessageRepository;
import com.ridetogether.server.domain.chat.dao.RedisRepository;
import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.chatroom.dao.ChatRoomMemberRepository;
import com.ridetogether.server.domain.chatroom.dao.ChatRoomRepository;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.chatroom.domain.ChatRoomMember;
import com.ridetogether.server.domain.chatroom.dto.PostGroupChatRoomRequest;
import com.ridetogether.server.domain.chatroom.dto.PostOneToOneChatRoomRequest;
import com.ridetogether.server.domain.matching.dao.MatchingRepository;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;
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

    @Transactional(rollbackOn = Exception.class)
    public void createGroupChatRoom(PostGroupChatRoomRequest request) {
        // 1. 매칭 존재 유무 체크
        Matching savedMatching = matchingRepository.findByIdx(request.getMatchingIdx())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MATCHING_NOT_FOUND));

        // 매칭 상태 확인 로직 짜야함

        // 2. 상대방 존재들 체크
        List<Member> groupMembers = new ArrayList<>();

        for(Long memberIdx : request.getAnotherMemberIdxs()) {
            groupMembers.add(memberRepository.findByIdx(memberIdx)
                    .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND)));
        }

        // 3. 방 존재 확인 함수
        if(request.getChatRoomIdx()!=null) {
            if (existGroupRoom(request.getChatRoomIdx(), savedMatching, groupMembers)) {
                ChatRoom existChatRoom = chatRoomRepository.findByIdxAndStatus(request.getChatRoomIdx(), ChatStatus.ACTIVE)
                        .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
                return;
            }
        }
        // 4. 존재하는 방 없다면 생성
        ChatRoom room = ChatRoom.builder()
                .roomHashCode(0)
                .matching(savedMatching)
                .build();
        chatRoomRepository.save(room);

        for(Member member : groupMembers) {
            ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                    .member(member)
                    .chatRoom(room)
                    .name(savedMatching.getTitle()) // TO - DO : 이름 방식 바꿔야함
                    .status(ChatStatus.ACTIVE)
                    .build();
            chatRoomMemberRepository.save(chatRoomMember);
        }

    }

    private int createRoomHashCode(Member member, Member anotherMember) {
        Long memberIdx = member.getIdx();
        Long anotherIdx = anotherMember.getIdx();
        return memberIdx > anotherIdx ? Objects.hash(memberIdx, anotherIdx) : Objects.hash(anotherIdx, memberIdx);
    }

    private boolean existRoom(int roomHashCode, Member member, Member anotherMember) {
        ChatRoom chatRoom = chatRoomRepository
                .findByRoomHashCodeAndStatus(roomHashCode, ChatStatus.ACTIVE).orElse(null);

        if(chatRoom != null) {
            List<ChatRoomMember> chatRoomMembers = chatRoom.getChatRoomMembers();

            if (chatRoomMembers.size() == 1) {
                //나만 있을 때
                if (chatRoomMembers.get(0).getMember().getIdx().equals(member.getIdx())) {
                    ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                            .member(anotherMember)
                            .chatRoom(chatRoom)
                            .name(member.getNickName())
                            .status(ChatStatus.ACTIVE)
                            .build();
                    chatRoomMemberRepository.save(chatRoomMember);
                } else {
                    //상대방만 있을 때
                    ChatRoomMember chatRoomAnotherMember = ChatRoomMember.builder()
                            .member(anotherMember)
                            .chatRoom(chatRoom)
                            .name(member.getNickName())
                            .status(ChatStatus.ACTIVE)
                            .build();
                    chatRoomMemberRepository.save(chatRoomAnotherMember);
                }
            }
            return true;
        }
        return false;
    }

    private boolean existGroupRoom(Long roomIdx, Matching matching, List<Member> members) {
        ChatRoom chatRoom = chatRoomRepository
                .findByIdx(roomIdx).orElse(null);

        if(chatRoom != null) {
            List<ChatRoomMember> chatRoomMembers= chatRoom.getChatRoomMembers();

            for(Member existsMember : members) {
                boolean check = false;
                for(ChatRoomMember chatRoomMember : chatRoomMembers ) {
                    if(chatRoomMember.getMember().getIdx().equals(existsMember.getIdx())) {
                        check = true;
                    }
                }
                if(check) {
                    ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                            .member(existsMember)
                            .chatRoom(chatRoom)
                            .name(matching.getTitle()) // 수정
                            .status(ChatStatus.ACTIVE)
                            .build();
                    chatRoomMemberRepository.save(chatRoomMember);
                }
            }

            return true;
        }
        return false;
    }
}
