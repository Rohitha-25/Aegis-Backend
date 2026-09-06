package com.agenticai.aegisagent.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class AgentTaskController {

    @Autowired
    private AgentTaskService taskService;

    @PostMapping
    public AgentTask executeTask(@RequestBody TaskRequest request) {
        return taskService.executeTask(request.getAgentId(), request.getRequest());
    }
}
