package com.agenticai.aegisagent.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @GetMapping
    public List<Agent> getAllAgents() {
        return agentService.getAllAgents();
    }

    @GetMapping("/{id}")
    public Agent getAgentById(@PathVariable Long id) {
        return agentService.getAgentById(id);
    }

    @PostMapping
    public Agent registerAgent(@RequestBody Agent agent) {
        return agentService.registerAgent(agent);
    }

    @PutMapping("/{id}/activate")
    public Agent activateAgent(@PathVariable Long id) {
        return agentService.activateAgent(id);
    }

    @PutMapping("/{id}/suspend")
    public Agent suspendAgent(@PathVariable Long id) {
        return agentService.suspendAgent(id);
    }

    @PutMapping("/{id}/revoke")
    public Agent revokeAgent(@PathVariable Long id) {
        return agentService.revokeAgent(id);
    }
}
