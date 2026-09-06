package com.agenticai.aegisagent.pam;

import com.agenticai.aegisagent.agent.Agent;
import com.agenticai.aegisagent.agent.AgentRepository;
import com.agenticai.aegisagent.audit.AuditDecision;
import com.agenticai.aegisagent.audit.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PAMService {

    @Autowired
    private PrivilegeRequestRepository privilegeRequestRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private AuditService auditService;

    public List<PrivilegeRequest> getAllPrivilegeRequests() {
        return privilegeRequestRepository.findAll();
    }

    public PrivilegeRequest requestPrivilege(Long agentId, String operation, String resource, String reason) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found."));

        PrivilegeRequest request = new PrivilegeRequest();
        request.setRequestId(
                "PR-" + UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase()
        );
        request.setAgent(agent);
        request.setOperation(operation);
        request.setResource(resource);
        request.setReason(reason);

        return privilegeRequestRepository.save(request);
    }

    public PrivilegeRequest approvePrivilege(String requestId) {
        PrivilegeRequest request = privilegeRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Privilege request not found."));

        request.setStatus(PrivilegeRequestStatus.APPROVED);
        request.setApprovedAt(LocalDateTime.now());

        // JIT access - valid for 5 mins
        request.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        PrivilegeRequest approvedRequest = privilegeRequestRepository.save(request);

        auditService.recordEvent(
                "PRIVILEGE_REQUEST",
                request.getAgent(),
                "privilege.approve",
                request.getResource(),
                AuditDecision.ALLOW,
                "Privileged access approved for JIT execution."
        );

        return approvedRequest;
    }

    public boolean isApprovedAndActive(String requestId) {
        PrivilegeRequest request = privilegeRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Privilege request not found."));

        if (request.getStatus() != PrivilegeRequestStatus.APPROVED) {
            return false;
        }

        if (request.getExpiresAt() ==  null || !request.getExpiresAt().isAfter(LocalDateTime.now())) {
            request.setStatus(PrivilegeRequestStatus.EXPIRED);
            privilegeRequestRepository.save(request);
            return false;
        }

        return true;
    }

    public String executePrivilege(String requestId) {
        PrivilegeRequest request = privilegeRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Privilege request not found."));

        if (!isApprovedAndActive(requestId)) {
            auditService.recordEvent(
                    "PRIVILEGED_OPERATION",
                    request.getAgent(),
                    request.getOperation(),
                    request.getResource(),
                    AuditDecision.DENY,
                    "Privilege request is not approved or has expired."
            );

            throw new SecurityException("Privilege request is not approved or has expired.");
        }

        // Simulating the restart rather than actually restarting.
        String result = "Privileged operation executed: "
                + request.getOperation()
                + " on "
                + request.getResource();

        request.setStatus(PrivilegeRequestStatus.EXPIRED);

        privilegeRequestRepository.save(request);

        auditService.recordEvent(
                "PRIVILEGED_OPERATION",
                request.getAgent(),
                request.getOperation(),
                request.getResource(),
                AuditDecision.ALLOW,
                "JIT privilege used successfully and expired."
        );

        return result;
    }
}
