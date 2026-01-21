package com.example.agent_rnd.controller; // [수정] api.notices 제거

import com.example.agent_rnd.domain.notice.ProjectNotice;
import com.example.agent_rnd.service.ProjectNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notices") // 주소는 그대로 유지!
@RequiredArgsConstructor
public class ProjectNoticeController {
    // ... 내용은 아까 작성하신 그대로 ...
    private final ProjectNoticeService projectNoticeService;

    @GetMapping
    public ResponseEntity<List<ProjectNotice>> getNotices() {
        return ResponseEntity.ok(projectNoticeService.getOpenNotices());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProjectNotice>> searchNotices(@RequestParam(name = "keyword") String keyword) {
        return ResponseEntity.ok(projectNoticeService.searchNotices(keyword));
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<ProjectNotice> getNoticeDetail(@PathVariable(name = "noticeId") Long noticeId) {
        return ResponseEntity.ok(projectNoticeService.getNoticeDetail(noticeId));
    }
}