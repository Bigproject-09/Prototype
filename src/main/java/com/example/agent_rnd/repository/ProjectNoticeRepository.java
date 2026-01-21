package com.example.agent_rnd.repository;

import com.example.agent_rnd.domain.notice.ProjectNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectNoticeRepository extends JpaRepository<ProjectNotice, Long> {

    // "모든 공고를 등록일 역순"으로 조회 (pubDate 기준)
    List<ProjectNotice> findAllByOrderByPubDateDesc();

    // 제목 검색
    List<ProjectNotice> findByTitleContaining(String keyword);
}