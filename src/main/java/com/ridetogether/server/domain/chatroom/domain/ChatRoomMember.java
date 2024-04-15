package com.ridetogether.server.domain.chatroom.domain;

import com.ridetogether.server.domain.chat.model.ChatStatus;
import com.ridetogether.server.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // 채팅방 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoom_idx")
    private ChatRoom chatRoom;

    private String name;

    @Enumerated(EnumType.STRING)
    private ChatStatus status;

    public void inActive() {
        this.status = ChatStatus.INACTIVE;
    }

}
