package com.agenticai.aegisagent.task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequest {
    private Long agentId;
    private String request;
}
