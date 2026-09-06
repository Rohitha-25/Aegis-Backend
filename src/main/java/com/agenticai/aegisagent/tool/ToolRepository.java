package com.agenticai.aegisagent.tool;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {
    Optional<Tool> findByToolName(String toolName);
    boolean existsByToolName(String toolName);
}
