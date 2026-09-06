package com.agenticai.aegisagent.tool;

import com.agenticai.aegisagent.agent.Agent;
import com.agenticai.aegisagent.agent.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolService {

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private AgentToolRepository agentToolRepository;

    public List<Tool> getAllTools() {
        return toolRepository.findAll();
    }

    public Tool getToolById(Long id) {
        return toolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tool not found."));
    }

    public List<AgentTool> getToolsForAgent(Long agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found."));

        return agentToolRepository.findByAgent(agent);
    }

    public Tool registerTool(Tool tool) {
        if (toolRepository.existsByToolName(tool.getToolName())) {
            throw new IllegalArgumentException("Tool name already exists.");
        }
        tool.setStatus(ToolStatus.REGISTERED);
        return toolRepository.save(tool);
    }

    public AgentTool assignToolToAgent(Long agentId, Long toolId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found."));

        Tool tool = getToolById(toolId);

        if (agentToolRepository.existsByAgentAndTool(agent, tool)) {
            throw new IllegalArgumentException("Tool already assigned to agent.");
        }

        AgentTool agentTool = new AgentTool();
        agentTool.setAgent(agent);
        agentTool.setTool(tool);

        return agentToolRepository.save(agentTool);
    }

    public boolean hasTool(Long agentId, String toolName) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found."));

        Tool tool = toolRepository.findByToolName(toolName)
                .orElseThrow(() -> new RuntimeException("Tool not found."));

        return agentToolRepository.existsByAgentAndTool(agent, tool);
    }

    public void removeToolFromAgent(Long agentId, Long toolId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found."));

        Tool tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new RuntimeException("Tool not found."));

        if (!agentToolRepository.existsByAgentAndTool(agent, tool)) {
            throw new RuntimeException("Tool is not assigned to this agent.");
        }

        agentToolRepository.deleteByAgentIdAndToolId(agentId, toolId);
    }
}
