package com.agenticai.aegisagent.workload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workloads")
public class WorkloadController {

    @Autowired
    private WorkloadService workloadService;

    @GetMapping
    public List<Workload> getAllWorkloads() {
        return workloadService.getAllWorkloads();
    }

    @GetMapping("/{id}")
    public Workload getWorkloadById(@PathVariable Long id) {
        return workloadService.getWorkloadById(id);
    }

    @GetMapping("/agent/{agentId}")
    public List<Workload> getWorkloadsForAgent(@PathVariable Long agentId) {
        return workloadService.getWorkloadsForAgent(agentId);
    }

    @PostMapping("/agent/{agentId}")
    public Workload registerWorkload(@PathVariable Long agentId, @RequestBody Workload workload) {
        return workloadService.registerWorkload(agentId, workload);
    }

    @PutMapping("/{id}/activate")
    public Workload activateWorkload(@PathVariable Long id) {
        return workloadService.activateWorkload(id);
    }

    @PutMapping("/{id}/suspend")
    public Workload suspendWorkload(@PathVariable Long id) {
        return workloadService.suspendWorkload(id);
    }

    @PutMapping("/{id}/revoke")
    public Workload revokeWorkload(@PathVariable Long id) {
        return workloadService.revokeWorkload(id);
    }
}
