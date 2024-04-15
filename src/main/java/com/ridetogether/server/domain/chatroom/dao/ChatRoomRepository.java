package com.ridetogether.server.domain.chatroom.dao;

import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByIdx(Long idx);
    Optional<ChatRoom> findByRoomHashCodeAndStatus(int roomHashCode, ChatStatus active);

    Optional<ChatRoom> findByIdxAndStatus(Long chatRoomIdx, ChatStatus active);

    Optional<ChatRoom> findByMatchingIdxAndStatus(Long matchingIdx,ChatStatus status);

    Optional<ChatRoom> findByIdxAndStatusAndRoomHashCodeGreaterThan(Long roomIdx, ChatStatus active, int i);

    Optional<ChatRoom> findByMatchingIdxAndStatusAndRoomHashCode(Long studyIdx, ChatStatus active, int i);
}
