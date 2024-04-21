package com.ridetogether.server.domain.matching.dto;

import lombok.Builder;
import lombok.Data;

public class MatchingResponseDto {

    @Builder
    @Data
    public static class CreateMatchingResponseDto {
        private Long matchingIdx;
        private String title;
        private Long memberIdx;
        private String memberNickName;
        private Long memberMatchingIdx;
        private Boolean isSuccess;
    }

    @Builder
    @Data
    public static class JoinMatchingResponseDto {
        private Long matchingIdx;
        private String title;
        private Long memberIdx;
        private String memberNickName;
        private Long hostMemberIdx;
        private String hostMemberNickName;
        private Long memberMatchingIdx;
        private Boolean isSuccess;
    }
}
