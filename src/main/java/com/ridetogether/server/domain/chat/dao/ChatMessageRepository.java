package com.ridetogether.server.domain.chat.dao;

import com.ridetogether.server.domain.chat.domain.ChatMessage;
import com.ridetogether.server.domain.chat.model.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByStatusAndChatRoomIdOrderByCreatedAtAsc(ChatStatus active, Long id);
}
