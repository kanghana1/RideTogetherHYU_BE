package com.ridetogether.server.domain.chatroom.dao;

import com.ridetogether.server.domain.chatroom.domain.ChatRoomMember;
import com.ridetogether.server.domain.chat.model.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findAllByUserIdAndStatus(Long id, ChatStatus active);
}
