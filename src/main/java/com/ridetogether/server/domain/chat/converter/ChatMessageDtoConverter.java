package com.ridetogether.server.domain.chat.converter;

import com.ridetogether.server.domain.chat.domain.ChatMessage;
import com.ridetogether.server.domain.chat.dto.ChatMessageDto;

public class ChatMessageDtoConverter {

    public static ChatMessageDto convertChatMessageToDto(ChatMessage chatMessage, Long chatRoomId) {
        return new ChatMessageDto(chatMessage.getType(), chatRoomId, chatMessage.getSenderIdx(), chatMessage.getSenderNickName(), chatMessage.getMessage(), chatMessage.getCreatedAt());
    }
}
