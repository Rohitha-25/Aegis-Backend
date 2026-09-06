package com.agenticai.aegisagent.audit;

import com.agenticai.aegisagent.agent.Agent;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuditService {

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    @Qualifier("auditVectorStore")
    private VectorStore auditVectorStore;

    public List<AuditEvent> getAllAuditEvents() {
        return auditRepository.findAllByOrderByTimestampDesc();
    }

    public AuditEvent recordEvent(
            String eventType,
            Agent agent,
            String action,
            String resource,
            AuditDecision decision,
            String reason
    ) {
        AuditEvent event = new AuditEvent();

        event.setEventType(eventType);
        event.setAgent(agent);
        event.setAction(action);
        event.setResource(resource);
        event.setDecision(decision);
        event.setReason(reason);

        AuditEvent savedEvent = auditRepository.save(event);

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
                savedEvent.getEventType(),
                savedEvent.getAction(),
                savedEvent.getResource(),
                savedEvent.getDecision(),
                savedEvent.getReason(),
                savedEvent.getTimestamp()
        );

        // Store metadata with the vector
        Map<String, Object> metadata = new HashMap<>();

        metadata.put(
                "auditEventId",
                savedEvent.getId().toString()
        );

        metadata.put(
                "agentIdentifier",
                agent.getAgentIdentifier()
        );

        metadata.put(
                "eventType",
                savedEvent.getEventType()
        );

        metadata.put(
                "decision",
                savedEvent.getDecision().name()
        );

        Document vectorDocument = new Document(
                auditText,
                metadata
        );

        auditVectorStore.add(
                List.of(vectorDocument)
        );

        return savedEvent;
    }
}
