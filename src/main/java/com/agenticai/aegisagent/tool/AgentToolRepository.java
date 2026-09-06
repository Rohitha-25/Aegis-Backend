package com.agenticai.aegisagent.tool;

import com.agenticai.aegisagent.agent.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AgentToolRepository extends JpaRepository<AgentTool,Long> {
    List<AgentTool> findByAgent(Agent agent);
    boolean existsByAgentAndTool(Agent agent, Tool tool);

    @Transactional
    void deleteByAgentIdAndToolId(Long agentId, Long toolId);
}
