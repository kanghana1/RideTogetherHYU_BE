package com.ridetogether.server.domain.matching.converter;

import com.ridetogether.server.domain.matching.dto.MatchingDto;
import com.ridetogether.server.domain.matching.dto.MatchingDto.CreateMatchingDto;
import com.ridetogether.server.domain.matching.dto.MatchingRequestDto;
import com.ridetogether.server.domain.matching.dto.MatchingRequestDto.CreateMatchingRequestDto;
import com.ridetogether.server.domain.member.model.Gender;
import com.ridetogether.server.domain.member.model.PayType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MatchingDtoConverter {

    public static CreateMatchingDto convertToCreateMatchingDto(CreateMatchingRequestDto createMatchingDto, Long memberIdx) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return CreateMatchingDto.builder()
                .hostMemberIdx(memberIdx)
                .title(createMatchingDto.getTitle())
                .ridingTime(createMatchingDto.getRidingTime())
                .departure(createMatchingDto.getDeparture())
                .destination(createMatchingDto.getDestination())
                .matchingGender(Gender.fromName(createMatchingDto.getMatchingGender()))
                .payTypes(createMatchingDto.getPayTypes().stream()
                        .map(PayType::fromName)
                        .toList()
                )
                .expiredAt(LocalDate.parse(createMatchingDto.getExpiredAt(), formatter))
                .build();
    }
}
