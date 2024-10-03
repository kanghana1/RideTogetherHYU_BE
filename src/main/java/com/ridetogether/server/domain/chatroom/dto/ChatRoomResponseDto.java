package com.ridetogether.server.domain.chatroom.dto;

import lombok.Builder;
import lombok.Data;

public class ChatRoomResponseDto {

    @Builder
    @Data
    public static class GetChatRoomResponseDto {
        private Long idx;
        private Long chatRoomId;
        private int userCount;
        private Long matchingIdx;
        private String matchingTitle;
    }

    @Builder
    @Data
    public static class CreateChatRoomResponseDto {
        private Long idx;
        private Long chatRoomId;
        private Long matchingIdx;
        private String matchingTitle;
        private boolean isSuccess;
    }
}
