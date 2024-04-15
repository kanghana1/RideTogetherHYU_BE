package com.ridetogether.server.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ridetogether.server.domain.chat.domain.ChatMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessageResponse {
    private Long memberIdx;
    private String nickName;
    private String message; // type이 image일 경우 객체 URL이 담김

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private Boolean isFile;

    public ChatMessageResponse(ChatMessage chatMessage) {
        this.memberIdx = chatMessage.getMember().getIdx();
        this.nickName = chatMessage.getMember().getNickName();
        this.message = chatMessage.getMessage();
        this.createdAt = chatMessage.getCreatedAt();
    }

    public ChatMessageResponse(ChatMessageRequest request) {
        this.memberIdx = request.getMemberIdx();
        this.nickName = request.getNickName();
        this.message = request.getMessage();
        this.isFile = request.getIsFile();
        this.createdAt = LocalDateTime.now(); // 현재시간 저장
    }
}
