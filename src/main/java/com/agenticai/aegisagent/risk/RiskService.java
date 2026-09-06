package com.agenticai.aegisagent.risk;

import com.agenticai.aegisagent.agent.Agent;
import com.agenticai.aegisagent.agent.AgentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RiskService {

    @Autowired
    private RiskRepository riskRepository;

    public RiskEvent recordRisk(
            Agent agent,
            String eventType,
            RiskLevel riskLevel,
            String reason
    ) {
        RiskEvent event = new RiskEvent();

        event.setAgent(agent);
        event.setEventType(eventType);
        event.setRiskLevel(riskLevel);
        event.setReason(reason);

        RiskEvent savedEvent = riskRepository.save(event);

        if (riskLevel == RiskLevel.CRITICAL) {
            agent.setStatus(AgentStatus.SUSPENDED);
        }

        return savedEvent;
    }
}
