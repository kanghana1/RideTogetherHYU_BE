package com.ridetogether.server.domain.chatroom.dao;

import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.matching.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByIdx(Long idx);
    Optional<ChatRoom> findByChatRoomId(Long chatRoomId);
    boolean existsByChatRoomId(Long chatRoomId);
    Optional<ChatRoom> findByMatchingAndChatStatus(Matching matching, ChatStatus active);
    Optional<ChatRoom> findByIdxAndChatStatus(Long chatRoomIdx, ChatStatus active);

    Optional<ChatRoom> findByMatchingIdxAndChatStatus(Long matchingIdx,ChatStatus status);

    Optional<ChatRoom> findByIdxAndChatStatusAndRoomHashCodeGreaterThan(Long roomIdx, ChatStatus active, int i);

    Optional<ChatRoom> findByMatchingIdxAndChatStatusAndRoomHashCode(Long studyIdx, ChatStatus active, int i);
}
