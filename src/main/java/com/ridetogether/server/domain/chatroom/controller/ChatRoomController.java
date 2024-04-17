package com.ridetogether.server.domain.chatroom.controller;

import com.ridetogether.server.domain.chat.application.ChatMessageService;
import com.ridetogether.server.domain.chat.dto.ChatMessageDto;
import com.ridetogether.server.domain.chatroom.application.ChatRoomService;
import com.ridetogether.server.domain.chatroom.converter.ChatRoomDtoConverter;
import com.ridetogether.server.domain.chatroom.domain.ChatRoom;
import com.ridetogether.server.domain.chatroom.dto.ChatRoomResponseDto;
import com.ridetogether.server.domain.chatroom.dto.ChatRoomResponseDto.GetChatRoomResponseDto;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @GetMapping("/rooms")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<GetChatRoomResponseDto>> rooms() {
        List<GetChatRoomResponseDto> responseDtos = chatRoomService.findAllRooms().stream()
                .map(ChatRoomDtoConverter::convertToGetChatRoomResponseDto)
                .toList();
        return ApiResponse.onSuccess(responseDtos);
    }

}
