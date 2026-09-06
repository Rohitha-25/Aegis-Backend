package com.agenticai.aegisagent.audit;

import com.agenticai.aegisagent.agent.Agent;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuditVectorStoreInitializer implements ApplicationRunner {

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    @Qualifier("auditVectorStore")
    private VectorStore auditVectorStore;

    @Override
    public void run(ApplicationArguments args) {

        List<AuditEvent> events =
                auditRepository.findAllWithAgent();

        for (AuditEvent event : events) {

            Agent agent = event.getAgent();

            if (agent == null) {
                continue;
            }

            String auditText = """
                    Agent: %s
                    Agent Name: %s
                    Event Type: %s
                    Action: %s
                    Resource: %s
                    Decision: %s
                    Reason: %s
                    Timestamp: %s
                    """.formatted(
                    agent.getAgentIdentifier(),
                    agent.getAgentName(),
                    event.getEventType(),
                    event.getAction(),
                    event.getResource(),
                    event.getDecision(),
                    event.getReason(),
                    event.getTimestamp()
            );

            Map<String, Object> metadata = new HashMap<>();

            metadata.put(
                    "auditEventId",
                    event.getId().toString()
            );

            metadata.put(
                    "agentIdentifier",
                    agent.getAgentIdentifier()
            );

            metadata.put(
                    "eventType",
                    event.getEventType()
            );

            metadata.put(
                    "decision",
                    event.getDecision().name()
            );

            Document vectorDocument =
                    new Document(
                            auditText,
                            metadata
                    );

            auditVectorStore.add(
                    List.of(vectorDocument)
            );
        }
    }
}