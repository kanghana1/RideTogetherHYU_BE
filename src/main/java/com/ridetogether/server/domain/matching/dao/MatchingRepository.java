package com.ridetogether.server.domain.matching.dao;

import com.ridetogether.server.domain.matching.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

}
