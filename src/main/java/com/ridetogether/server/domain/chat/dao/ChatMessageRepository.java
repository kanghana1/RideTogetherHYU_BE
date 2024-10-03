package com.ridetogether.server.domain.chat.dao;

import com.ridetogether.server.domain.chat.domain.ChatMessage;
import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatRoomIdxOrderByCreatedAtAsc(Long chatRoomIdx);

    List<ChatMessage> findByIdx(Long idx);

    List<ChatMessage> findAllBySenderIdxOrderByCreatedAtAsc(Long senderIdx);

    List<ChatMessage> findTop100ByChatRoomIdxOrderByCreatedAtDesc(Long chatRoomIdx);
}
