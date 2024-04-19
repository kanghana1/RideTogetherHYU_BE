package com.ridetogether.server.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto implements Serializable {

    // 메시지 타입 : 채팅
    public enum MessageType {
        ENTER,
        TALK,
        LEAVE,
    }

    private MessageType type; // 메시지 타입
    private Long chatRoomId; // 공통으로 만들어진 방 번호
    private Long senderIdx;
    private String senderNickName;
    private String roomTitle; // 매칭 제목
    private String message; // 메시지
    private LocalDateTime createdAt; // 메시지 생성 시간

    // 이미지 향후 추가


    public ChatMessageDto(MessageType type, Long chatRoomId, Long senderIdx, String senderNickName, String message, LocalDateTime createdAt) {
        this.type = type;
        this.chatRoomId = chatRoomId;
        this.senderIdx = senderIdx;
        this.senderNickName = senderNickName;
        this.message = message;
        this.createdAt = createdAt;
    }
}
