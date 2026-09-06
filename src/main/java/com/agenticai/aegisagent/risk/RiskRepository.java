package com.agenticai.aegisagent.risk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskRepository extends JpaRepository<RiskEvent, Long> {
    List<RiskEvent> findAllByOrderByCreatedAtDesc();
}
