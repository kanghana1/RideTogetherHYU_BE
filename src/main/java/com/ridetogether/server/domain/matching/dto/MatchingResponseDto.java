package com.ridetogether.server.domain.matching.dto;

import lombok.Builder;
import lombok.Data;

public class MatchingResponseDto {

    @Builder
    @Data
    public static class CreateMatchingResponseDto {
        private Long matchingIdx;
        private Long hostMemberIdx;
        private Long memberMatchingIdx;
        private String hostMemberNickName;
        private Boolean isSuccess;
    }
}
