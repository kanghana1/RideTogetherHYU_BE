package com.ridetogether.server.domain.matchingroom.dao;

import com.ridetogether.server.domain.matchingroom.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    Optional<Matching> findByIdx(Long idx);
}
