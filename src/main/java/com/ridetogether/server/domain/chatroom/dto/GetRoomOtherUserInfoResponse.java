package com.ridetogether.server.domain.chatroom.dto;

import com.ridetogether.server.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "다른 유저 정보 조회 DTO")
public class GetRoomOtherUserInfoResponse {

    private Long otherUserIdx;
    private String otherMemberNickName;

    public GetRoomOtherUserInfoResponse(Member otherUser) {
        this.otherUserIdx = otherUser.getIdx();
        this.otherMemberNickName = otherUser.getNickName();
    }
}
