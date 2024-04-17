package com.ridetogether.server.domain.chat.domain;

import com.ridetogether.server.domain.chat.dto.ChatMessageDto.MessageType;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private MessageType type;

    private Long senderIdx;

    private String senderNickName;

    private String message;

//    @OneToMany(mappedBy = "chatMessage")
//    @JsonIgnore
//    private List<Image> images;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    public static ChatMessage createChatMessage(ChatRoom chatRoom, Long senderIdx, String senderNickName, String message, MessageType type) {
        return ChatMessage.builder()
                .type(type)
                .senderIdx(senderIdx)
                .senderNickName(senderNickName)
                .message(message)
                .chatRoom(chatRoom)
                .build();
    }

}
