package com.ridetogether.server.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest implements Serializable {

    // 메시지 타입 : 채팅
    public enum MessageType {
        TALK,
        GROUP_TALK,
        UNREAD_MESSAGE_COUNT_ALARM
    }

    private Long messageIdx;
    private String roomTitle; // 그룹채팅일 경우 매칭 제목
    private MessageType type; // 메시지 타입
    private String nickName;
    private Long roomIdx; // 공통으로 만들어진 방 번호
    private Set<Long> otherMemberIds; // 상대방
    private String message; // 메시지
    private Long memberIdx;
    private int count;
    private Boolean isFile;

    // 안 읽은 메세지 처리를 위한 생성자
    public ChatMessageRequest(ChatMessageRequest chatMessageDto, int count) {
        this.type = MessageType.UNREAD_MESSAGE_COUNT_ALARM; // 메시지 타입
        this.roomIdx = chatMessageDto.roomIdx; // 방 이름
        this.otherMemberIds = chatMessageDto.otherMemberIds; // 상대방 privateKey
        this.count = count; //안읽은 메세지 개수
    }
}
