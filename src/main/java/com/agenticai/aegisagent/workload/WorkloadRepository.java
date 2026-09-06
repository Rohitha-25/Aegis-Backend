package com.agenticai.aegisagent.workload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkloadRepository extends JpaRepository<Workload,Long> {
    List<Workload> findByAgentId(Long agentId);
    boolean existsByWorkloadId(String workloadId);
    boolean existsBySpiffeId(String spiffeId);
}
