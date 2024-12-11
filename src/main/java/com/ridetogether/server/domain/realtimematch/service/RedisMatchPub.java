package com.ridetogether.server.domain.realtimematch.service;

import com.ridetogether.server.domain.realtimematch.domain.RealTimeMatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisMatchPub {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic channelTopic, RealTimeMatch realTimeMatch) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), realTimeMatch);
    }
}
