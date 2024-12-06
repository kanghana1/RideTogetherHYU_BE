package com.ridetogether.server.domain.realtimematch.dto;

import com.ridetogether.server.domain.matching.model.MatchingStatus;
import com.ridetogether.server.domain.realtimematch.model.MatchMemberStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RealTimeMatchDto implements Serializable {

    private MatchMemberStatus participantStatus; // 이게 꼭 필요할까 ? -> 이게 없어지면 dto를 따로 만들 필요 없음

    private MatchingStatus matchingStatus;

    private Long realTimeMatchId;

    private Long participantId;

    private Set<Long> restParticipantsIds = new HashSet<>();

    private int nowParticipantCnt;

    private int maxParticipantCnt; // Matching에서 가져와서 저장 해두기

    private LocalDate expiredAt;

}
