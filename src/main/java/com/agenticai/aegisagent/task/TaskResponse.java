package com.agenticai.aegisagent.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaskResponse {
    private String taskId;
    private Long agentId;
    private String request;
    private String response;
    private TaskStatus status;
}
