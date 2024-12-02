package com.ridetogether.server.domain.matchingroom.dto;

import lombok.Builder;
import lombok.Data;


import java.util.List;

public class MatchingRequestDto {

    @Builder
    @Data
    public static class CreateMatchingRequestDto{
        private String title;
        private String ridingTime;
        private String departure;
        private String destination;
        private String matchingGender;
        private List<String> payTypes;
        private String expiredAt;
    }

    @Builder
    @Data
    public static class UpdatePriceRequestDto {
        private int price;
    }
}
