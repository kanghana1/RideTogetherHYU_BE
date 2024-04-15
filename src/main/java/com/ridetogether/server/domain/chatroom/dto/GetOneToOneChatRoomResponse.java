package com.ridetogether.server.domain.chatroom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "일대일 채팅방 조회 DTO")
public class GetOneToOneChatRoomResponse {
    private String roomName;
    private String chatRoomIdx;
    private String lastMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    private String dayBefore;
    private int unreadCount;

    public GetOneToOneChatRoomResponse(
            String roomName,
            String chatRoomIdx,
            String lastMessage,
            LocalDateTime createAt,
            String dayBefore,
            int unreadCount
    ) {
        this.roomName = roomName;
        this.chatRoomIdx = chatRoomIdx;
        this.lastMessage = lastMessage;
        this.createAt = createAt;
        this.dayBefore = dayBefore;
        this.unreadCount = unreadCount;
    }
}
