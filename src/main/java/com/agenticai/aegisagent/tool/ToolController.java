package com.agenticai.aegisagent.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
public class ToolController {

    @Autowired
    private ToolService toolService;

    @GetMapping
    public List<Tool> getAllTools() {
        return toolService.getAllTools();
    }

    @GetMapping("/{id}")
    public Tool getToolById(@PathVariable Long id) {
        return toolService.getToolById(id);
    }

    @GetMapping("/agent/{agentId}")
    public List<AgentTool> getToolsForAgent(@PathVariable Long agentId) {
        return toolService.getToolsForAgent(agentId);
    }

    @PostMapping
    public Tool registerTool(@RequestBody Tool tool) {
        return toolService.registerTool(tool);
    }

    @PostMapping("/agent/{agentId}/tool/{toolId}")
    public AgentTool assignToolToAgent(@PathVariable Long agentId, @PathVariable Long toolId) {
        return toolService.assignToolToAgent(agentId, toolId);
    }

    @DeleteMapping("/agent/{agentId}/tool/{toolId}")
    public void removeToolFromAgent(@PathVariable Long agentId, @PathVariable Long toolId) {
        toolService.removeToolFromAgent(agentId, toolId);
    }
}
