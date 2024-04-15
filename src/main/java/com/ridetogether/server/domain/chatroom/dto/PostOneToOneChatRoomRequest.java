package com.ridetogether.server.domain.chatroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "일대일 채팅방 생성 Request")
public class PostOneToOneChatRoomRequest {
    // 상대방
    @NotBlank(message = "상대방 아이디를 입력해주세요")
    @Schema(description = "상 멤버", nullable = false)
    private Long anotherMemberIdx;
}
