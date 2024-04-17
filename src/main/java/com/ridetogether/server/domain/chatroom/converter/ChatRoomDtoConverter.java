package com.ridetogether.server.domain.chatroom.converter;

import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.chatroom.dto.ChatRoomResponseDto;
import com.ridetogether.server.domain.chatroom.dto.ChatRoomResponseDto.GetChatRoomResponseDto;

public class ChatRoomDtoConverter {

    public static GetChatRoomResponseDto convertToGetChatRoomResponseDto(ChatRoom chatRoom) {
        return GetChatRoomResponseDto.builder()
                .idx(chatRoom.getIdx())
                .chatRoomId(chatRoom.getChatRoomId())
                .userCount(chatRoom.getUserCount())
                .matchingIdx(chatRoom.getMatching().getIdx())
                .matchingTitle(chatRoom.getMatching().getTitle())
                .build();
    }

}
