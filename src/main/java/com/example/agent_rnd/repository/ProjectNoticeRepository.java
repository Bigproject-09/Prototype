package com.example.agent_rnd.repository;

import com.example.agent_rnd.domain.notice.ProjectNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectNoticeRepository extends JpaRepository<ProjectNotice, Long> {
}
