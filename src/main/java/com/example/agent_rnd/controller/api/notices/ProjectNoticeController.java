package com.example.agent_rnd.controller.api.notices;

import com.example.agent_rnd.domain.notice.ProjectNotice;
import com.example.agent_rnd.service.ProjectNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class ProjectNoticeController {

    private final ProjectNoticeService projectNoticeService;

    // 1. 공고 목록 조회 (검색 기능 포함)
    // 사용법: /api/notices?keyword=AI (검색) 또는 /api/notices (전체)
    @GetMapping
    public List<ProjectNotice> getNotices(@RequestParam(required = false) String keyword) {
        return projectNoticeService.searchNotices(keyword);
    }

    // 2. 공고 상세 조회
    // 사용법: /api/notices/1
    @GetMapping("/{id}")
    public ProjectNotice getNoticeDetail(@PathVariable Long id) {
        return projectNoticeService.getNoticeDetail(id);
    }
}