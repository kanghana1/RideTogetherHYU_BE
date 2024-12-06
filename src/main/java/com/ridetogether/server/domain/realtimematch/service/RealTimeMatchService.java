package com.ridetogether.server.domain.realtimematch.service;

import com.ridetogether.server.domain.realtimematch.dto.RealTimeMatchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimeMatchService {

    private final RedisTemplate<Long, RealTimeMatchDto> redisTemplate;

    /*
    필요기능
    1. 매칭 생성
    2. 매칭 삭제
    3. 매칭 조회
    4. 인원 추가
    5. 인원 제거
    * */

}
