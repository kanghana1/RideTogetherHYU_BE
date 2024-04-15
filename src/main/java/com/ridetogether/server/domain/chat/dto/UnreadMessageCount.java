package com.ridetogether.server.domain.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnreadMessageCount {
    private Long otherUserId;
    private int unreadCount;
    private Long roomId;
    private String type;

    public UnreadMessageCount(ChatMessageRequest roomMessage) {
        this.type = "UNREAD";
        this.otherUserId = roomMessage.getOtherMemberIds().stream().toList().get(0);
        this.roomId = roomMessage.getRoomIdx();
        this.unreadCount = roomMessage.getCount();
    }
}
