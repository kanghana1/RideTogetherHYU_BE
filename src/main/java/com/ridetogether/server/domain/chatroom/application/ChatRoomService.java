package com.ridetogether.server.domain.chatroom.application;

import com.ridetogether.server.domain.chat.dao.ChatMessageRepository;
import com.ridetogether.server.domain.chat.dao.RedisRepository;
import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.chatroom.dao.ChatRoomMemberRepository;
import com.ridetogether.server.domain.chatroom.dao.ChatRoomRepository;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.chatroom.domain.ChatRoomMember;
import com.ridetogether.server.domain.matching.dao.MatchingRepository;
import com.ridetogether.server.domain.matching.dao.MemberMatchingRepository;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.domain.matching.domain.MemberMatching;
import com.ridetogether.server.domain.matching.model.MatchingStatus;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;
    private final MemberMatchingRepository memberMatchingRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisRepository redisRepository;

    @Transactional(rollbackOn = Exception.class)
    public Long createChatRoom(Long memberIdx, Long anotherMemberIdx) {
        // 1. 나와 상대방 존재 체크
        Member member = memberRepository.findByIdx(memberIdx).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Member anotherMember = memberRepository.findByIdx(anotherMemberIdx).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 2. roomHashCode 만들기
        int roomHashCode = createRoomHashCode(memberIdx, anotherMemberIdx);

        // 3. 방 존재 확인
        if(existRoomAndAddMember(roomHashCode, member, anotherMember)) {
            ChatRoom savedChatRoom = chatRoomRepository.findByRoomHashCodeAndChatStatus(roomHashCode, ChatStatus.ACTIVE)
                    .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
            return savedChatRoom.getIdx();
        }

        // 4. 존재하는 방 없다면 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .roomHashCode(roomHashCode)
                .chatStatus(ChatStatus.ACTIVE)
                .build();
        chatRoomRepository.save(chatRoom);

        // 5. 해당 유저 채팅 매핑 데이터 생성
        ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                .member(member)
                .chatRoom(chatRoom)
                .name(anotherMember.getNickName())
                .status(ChatStatus.ACTIVE)
                .build();
        // 6. 상대방 유저 채팅 매핑 데이터 생성
        ChatRoomMember chatRoomAnotherMember = ChatRoomMember.builder()
                .member(anotherMember)
                .chatRoom(chatRoom)
                .name(member.getNickName())
                .status(ChatStatus.ACTIVE)
                .build();

        chatRoom.getChatRoomMembers().add(chatRoomMember);
        chatRoom.getChatRoomMembers().add(chatRoomAnotherMember);

        chatRoomMemberRepository.save(chatRoomMember);
        chatRoomMemberRepository.save(chatRoomAnotherMember);

        return chatRoom.getIdx();
    }

    @Transactional(rollbackOn = Exception.class)
    public Long createGroupChatRoom(Long memberIdx, Long matchingIdx) {
        // 1. 매칭 존재 유무 체크
        Member member = memberRepository.findByIdx(memberIdx)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Matching savedMatching = matchingRepository.findByIdx(matchingIdx)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MATCHING_NOT_FOUND));

        // 매칭 상태 확인 로직
        if (!savedMatching.getMatchingStatus().equals(MatchingStatus.FINISH)) {
            throw new ErrorHandler(ErrorStatus.MATCHING_ALREADY_FINISH);
        }

        // 3. 방 존재 확인 함수
        if (existGroupRoomAndAddMember(savedMatching)) {
            ChatRoom existChatRoom = chatRoomRepository.findByMatchingAndChatStatus(savedMatching, ChatStatus.ACTIVE)
                    .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
            return existChatRoom.getIdx();
        }
        // 4. 존재하는 방 없다면 생성
        ChatRoom room = ChatRoom.builder()
                .roomHashCode(0)
                .matching(savedMatching)
                .build();
        chatRoomRepository.save(room);


        ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                .member(member)
                .chatRoom(room)
                .name(savedMatching.getTitle())
                .status(ChatStatus.ACTIVE)
                .build();
        chatRoomMemberRepository.save(chatRoomMember);

        room.getChatRoomMembers().add(chatRoomMember);

        return room.getIdx();

    }

    private int createRoomHashCode(Long memberIdx, Long anotherMemberIdx) {
        return memberIdx > anotherMemberIdx ? Objects.hash(memberIdx, anotherMemberIdx) : Objects.hash(anotherMemberIdx, memberIdx);
    }

    private boolean existRoomAndAddMember(int roomHashCode, Member member, Member anotherMember) {
        ChatRoom chatRoom = chatRoomRepository
                .findByRoomHashCodeAndChatStatus(roomHashCode, ChatStatus.ACTIVE).orElse(null);

        // 채팅방이 존재하는데 연결이 되어있지 않을 때
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

    private boolean existGroupRoomAndAddMember(Matching matching) {
        ChatRoom chatRoom = chatRoomRepository
                .findByMatchingAndChatStatus(matching, ChatStatus.ACTIVE).orElse(null);

        List<MemberMatching> memberMatchings = memberMatchingRepository.findAllByMatching(matching);
        List<Member> members = memberMatchings.stream().map(MemberMatching::getMember).toList();

        if(chatRoom != null && !members.isEmpty()) {
            List<ChatRoomMember> chatRoomMembers = chatRoom.getChatRoomMembers();
            List<Member> groupMembers = memberMatchings.stream().map(MemberMatching::getMember).toList();

            for(Member existsMember : members) {
                boolean check = false;
                if (!groupMembers.contains(existsMember)) {
                    ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                            .member(existsMember)
                            .chatRoom(chatRoom)
                            .name(matching.getTitle()) // 수정
                            .status(ChatStatus.ACTIVE)
                            .build();
                    chatRoomMemberRepository.save(chatRoomMember);

                    chatRoom.getChatRoomMembers().add(chatRoomMember);
                }
            }

            return true;
        }
        return false;
    }
}
