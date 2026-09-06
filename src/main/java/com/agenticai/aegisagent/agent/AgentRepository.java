package com.agenticai.aegisagent.agent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentRepository extends JpaRepository<Agent,Long> {
    boolean existsByAgentIdentifier(String agentIdentifier);

    @Query ("""
        SELECT a FROM Agent a
        ORDER BY
        CASE
            WHEN a.status = com.agenticai.aegisagent.agent.AgentStatus.ACTIVE THEN 0
            ELSE 1
        END,
        a.agentName ASC
""")
    List<Agent> findAllAgentsOrdered();
}
