package com.agenticai.aegisagent.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditEvent, Long> {
    List<AuditEvent> findAllByOrderByTimestampDesc();

    @Query("""
            SELECT ae
            FROM AuditEvent ae
            LEFT JOIN FETCH ae.agent
            ORDER BY ae.timestamp DESC
    """)
    List<AuditEvent> findAllWithAgent();
}
