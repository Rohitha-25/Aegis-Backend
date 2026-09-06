package com.agenticai.aegisagent.pam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivilegeRequestRepository extends JpaRepository<PrivilegeRequest,Long> {
    Optional<PrivilegeRequest> findByRequestId(String requestId);
}
