package com.example.agent_rnd.repository;

import com.example.agent_rnd.domain.requirement.RequirementProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequirementProgressRepository extends JpaRepository<RequirementProgress, Long> {
    List<RequirementProgress> findByProposalId(Long proposalId);
}