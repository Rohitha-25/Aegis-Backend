package com.agenticai.aegisagent.workload;

import com.agenticai.aegisagent.agent.Agent;
import com.agenticai.aegisagent.agent.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkloadService {

    @Autowired
    private WorkloadRepository workloadRepository;

    @Autowired
    private AgentRepository agentRepository;

    public List<Workload> getAllWorkloads() {
        return workloadRepository.findAll();
    }

    public Workload getWorkloadById(Long id) {
        return workloadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workload not found."));
    }

    public List<Workload> getWorkloadsForAgent(Long agentId) {
        return workloadRepository.findByAgentId(agentId);
    }

    public Workload registerWorkload(Long agentId, Workload workload) {
        if (workloadRepository.existsByWorkloadId(workload.getWorkloadId())) {
            throw new IllegalArgumentException("Workload ID already exists.");
        }

        if (workload.getSpiffeId() != null && workloadRepository.existsBySpiffeId(workload.getSpiffeId())) {
            throw new IllegalArgumentException("SPIFFE ID already exists.");
        }

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found."));

        workload.setAgent(agent);
        workload.setStatus(WorkloadStatus.REGISTERED);

        return workloadRepository.save(workload);
    }

    public Workload activateWorkload(Long id) {
        Workload workload = getWorkloadById(id);
        workload.setStatus(WorkloadStatus.ACTIVE);
        return workloadRepository.save(workload);
    }

    public Workload suspendWorkload(Long id) {
        Workload workload = getWorkloadById(id);
        workload.setStatus(WorkloadStatus.SUSPENDED);
        return workloadRepository.save(workload);
    }

    public Workload revokeWorkload(Long id) {
        Workload workload = getWorkloadById(id);
        workload.setStatus(WorkloadStatus.REVOKED);
        return workloadRepository.save(workload);
    }
}
