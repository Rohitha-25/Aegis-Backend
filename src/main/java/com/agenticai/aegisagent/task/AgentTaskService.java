package com.agenticai.aegisagent.task;

import com.agenticai.aegisagent.agent.Agent;
import com.agenticai.aegisagent.agent.AgentRepository;
import com.agenticai.aegisagent.agent.AgentStatus;
import com.agenticai.aegisagent.audit.AuditDecision;
import com.agenticai.aegisagent.audit.AuditService;
import com.agenticai.aegisagent.audit.AuditVectorSearchService;
import com.agenticai.aegisagent.document.DocumentVectorSearchService;
import com.agenticai.aegisagent.integration.llm.LLMService;
import com.agenticai.aegisagent.pam.PAMService;
import com.agenticai.aegisagent.pam.PrivilegeRequest;
import com.agenticai.aegisagent.risk.RiskLevel;
import com.agenticai.aegisagent.risk.RiskService;
import com.agenticai.aegisagent.tool.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AgentTaskService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private AgentTaskRepository taskRepository;

    @Autowired
    private ToolService toolService;

    @Autowired
    private LLMService  llmService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private PAMService pamService;

    @Autowired
    private RiskService riskService;

    @Autowired
    private DocumentVectorSearchService documentVectorSearchService;

    @Autowired
    private AuditVectorSearchService auditVectorSearchService;

    public AgentTask executeTask(Long agentId, String request) {

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found."));

        if (agent.getStatus() != AgentStatus.ACTIVE) {
            throw new IllegalStateException("Agent is not active.");
        }

        AgentTask task = new AgentTask();

        task.setTaskId(
                "TASK-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase()
        );

        task.setAgent(agent);
        task.setRequest(request);
        task.setStatus(TaskStatus.RUNNING);

        AgentTask savedTask = taskRepository.save(task);

        try {

            String response;

            if ("ORACLE".equalsIgnoreCase(agent.getAgentIdentifier())) {
                boolean hasSearchTool = toolService.hasTool(agent.getId(), "document.search");

                if (!hasSearchTool) {
                    auditService.recordEvent(
                            "TOOL_EXECUTION",
                            agent,
                            "document.search",
                            request,
                            AuditDecision.DENY,
                            "Tool is not assigned to agent."
                    );

                    riskService.recordRisk(
                            agent,
                            "UNAUTHORIZED_TOOL_ATTEMPT",
                            RiskLevel.HIGH,
                            "Agent attempted to use a tool that was not assigned."
                    );

                    throw new SecurityException("Oracle is not authorized to use document.search!");
                }

                auditService.recordEvent(
                        "TOOL_EXECUTION",
                        agent,
                        "document.search",
                        request,
                        AuditDecision.ALLOW,
                        "Tool authorized for agent."
                );

                var documents = documentVectorSearchService.search(request);

                if (documents.isEmpty()) {
                    response = "Oracle could not find any relevant information " +
                                "in the approved enterprise knowledge base.";
                } else {
                    StringBuilder context = new StringBuilder();

                    documents.forEach(document ->
                            context.append("Source: ")
                                    .append(document.getMetadata().getOrDefault(
                                            "title",
                                            "Enterprise Document"
                                    ))
                                    .append("\n")
                                    .append(document.getText())
                                    .append("\n\n")
                    );

                    response = llmService.generateResponse("Oracle", "Knowledge and document retrieval", request, context.toString());
                }
            } else if ("SHADOW".equalsIgnoreCase(agent.getAgentIdentifier())) {
                boolean hasAuditSearchTool = toolService.hasTool(agent.getId(), "audit.search");

                if (!hasAuditSearchTool) {
                    auditService.recordEvent(
                            "TOOL_EXECUTION",
                            agent,
                            "audit.search",
                            request,
                            AuditDecision.DENY,
                            "Tool is not assigned to agent."
                    );

                    riskService.recordRisk(
                            agent,
                            "UNAUTHORIZED_TOOL_ATTEMPT",
                            RiskLevel.HIGH,
                            "Agent attempted to use a tool that was not assigned."
                    );

                    throw new SecurityException("Shadow is not authorized to use audit.search!");
                }

                auditService.recordEvent(
                        "TOOL_EXECUTION",
                        agent,
                        "audit.search",
                        request,
                        AuditDecision.ALLOW,
                        "Tool authorized for agent."
                );

                var events = auditVectorSearchService.search(request);

                if (events.isEmpty()) {
                    response = "Shadow could not find any relevant security activity.";
                } else {
                    StringBuilder context = new StringBuilder();

                    events.forEach(event ->
                            context.append(event.getText())
                                    .append("\n\n")
                    );

                    response = llmService.generateResponse("Shadow", "Security investigation and audit analysis", request, context.toString());
                }
            } else if ("TITAN".equalsIgnoreCase(agent.getAgentIdentifier())) {
                boolean hasRestartTool = toolService.hasTool(agent.getId(), "service.restart");

                if (!hasRestartTool) {
                    auditService.recordEvent(
                            "TOOL_EXECUTION",
                            agent,
                            "service.restart",
                            request,
                            AuditDecision.DENY,
                            "Tool is not assigned to agent."
                    );

                    riskService.recordRisk(
                            agent,
                            "UNAUTHORIZED_TOOL_ATTEMPT",
                            RiskLevel.HIGH,
                            "Agent attempted to use a tool that was not assigned."
                    );

                    throw new SecurityException("Titan is not authorized to use service.restart!");
                }

                PrivilegeRequest privilegeRequest = pamService.requestPrivilege(
                        agent.getId(),
                        "service.restart",
                        "demo-service",
                        request
                );

                response = "Titan requested privileged access."
                        + "Approval required. Request ID: "
                        + privilegeRequest.getRequestId();
            } else {
                response = "Agent received the request: " + request;
            }

            savedTask.setResponse(response);
            savedTask.setStatus(TaskStatus.COMPLETED);
            savedTask.setCompletedAt(LocalDateTime.now());

            AgentTask completedTask = taskRepository.save(savedTask);

            auditService.recordEvent(
                    "AGENT_TASK",
                    agent,
                    "task.execute",
                    completedTask.getTaskId(),
                    AuditDecision.ALLOW,
                    "Task completed successfully."
            );

            return completedTask;

        } catch (Exception exception) {
            savedTask.setStatus(TaskStatus.FAILED);
            savedTask.setCompletedAt(LocalDateTime.now());

            taskRepository.save(savedTask);

            String reason = exception.getMessage() != null
                    ? exception.getMessage() : exception.getClass().getSimpleName();

            auditService.recordEvent(
                    "AGENT_TASK",
                    agent,
                    "task.execute",
                    savedTask.getTaskId(),
                    AuditDecision.DENY,
                    reason
            );

            throw exception;
        }
    }
}
