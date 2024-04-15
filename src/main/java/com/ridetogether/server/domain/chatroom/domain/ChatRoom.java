package com.ridetogether.server.domain.chatroom.domain;

import com.ridetogether.server.domain.chat.domain.ChatMessage;
import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom extends BaseTimeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private Long hostMemberIdx;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    // 특정 다른 유저와 해당 유저의 채팅방 값
    // 그룹 채팅 시 0
    private int roomHashCode;

    @ManyToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "matching_idx")
    private Matching matching;

    @Enumerated(EnumType.STRING)
    private ChatStatus status;

    public void addChatMessage(ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
    }

    public ChatRoom inActive() {
        this.status = ChatStatus.INACTIVE;
        return this;
    }


}
