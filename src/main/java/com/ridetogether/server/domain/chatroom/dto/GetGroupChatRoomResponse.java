package com.ridetogether.server.domain.chatroom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetGroupChatRoomResponse {
    private String roomName;
    private String chatRoomIdx;
    private String lastMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    private String dayBefore;

    public GetGroupChatRoomResponse(
            String roomName,
            String chatRoomIdx,
            String lastMessage,
            LocalDateTime createAt,
            String dayBefore
    ) {
        this.roomName = roomName;
        this.chatRoomIdx = chatRoomIdx;
        this.lastMessage = lastMessage;
        this.createAt = createAt;
        this.dayBefore = dayBefore;
    }
}
