package com.agenticai.aegisagent.pam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pam")
public class PAMController {

    @Autowired
    private PAMService pamService;

    @GetMapping
    public List<PrivilegeRequest> getAllPrivilegeRequests() {
        return pamService.getAllPrivilegeRequests();
    }

    @GetMapping("/{requestId}/active")
    public boolean isApprovedAndActive(@PathVariable String requestId) {
        return pamService.isApprovedAndActive(requestId);
    }

    @PostMapping("/request")
    public PrivilegeRequest requestPrivilege(
            @RequestParam Long agentId,
            @RequestParam String operation,
            @RequestParam String resource,
            @RequestParam String reason
    ) {
        return pamService.requestPrivilege(agentId, operation, resource, reason);
    }

    @PostMapping("/{requestId}/approve")
    public PrivilegeRequest approvePrivilege(@PathVariable String requestId) {
        return pamService.approvePrivilege(requestId);
    }

    @PostMapping("/{requestId}/execute")
    public String executePrivilege(@PathVariable String requestId) {
        return pamService.executePrivilege(requestId);
    }
}
