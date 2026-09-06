package com.agenticai.aegisagent.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;

    public List<Agent> getAllAgents() {
        return agentRepository.findAllAgentsOrdered();
    }

    public Agent getAgentById(Long id) {
        return agentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Agent not found!"));
    }

    public Agent registerAgent(Agent agent) {
        if (agentRepository.existsByAgentIdentifier(agent.getAgentIdentifier())) {
            throw new RuntimeException("Agent identifier already exists.");
        }
        agent.setStatus(AgentStatus.REGISTERED);
        return agentRepository.save(agent);
    }

    public Agent activateAgent(Long id) {
        Agent agent = getAgentById(id);
        agent.setStatus(AgentStatus.ACTIVE);
        return agentRepository.save(agent);
    }

    public Agent suspendAgent(Long id) {
        Agent agent = getAgentById(id);
        agent.setStatus(AgentStatus.SUSPENDED);
        return agentRepository.save(agent);
    }

    public Agent revokeAgent(Long id) {
        Agent agent = getAgentById(id);
        agent.setStatus(AgentStatus.REVOKED);
        return agentRepository.save(agent);
    }
}
