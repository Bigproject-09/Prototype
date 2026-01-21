package com.example.agent_rnd.controller;

import com.example.agent_rnd.domain.proposal.Proposal;
import com.example.agent_rnd.service.ProposalService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    // [추가됨] 1. 제안서 생성 요청 (AI 작업 시작)
    // POST /api/proposals
    @PostMapping
    public ResponseEntity<Long> createProposal(@RequestBody CreateProposalRequest request) {
        Long proposalId = proposalService.createProposal(
                request.getUserId(),
                request.getNoticeId(),
                request.getTemplateId(),
                request.getTitle()
        );
        return ResponseEntity.ok(proposalId);
    }

    // 2. 내 제안서 목록 조회
    // GET /api/proposals/my/1
    @GetMapping("/my/{userId}")
    public ResponseEntity<List<Proposal>> getMyProposals(@PathVariable Long userId) {
        return ResponseEntity.ok(proposalService.getMyProposals(userId));
    }

    // 3. 제안서 상세 조회
    // GET /api/proposals/5
    @GetMapping("/{id}")
    public ResponseEntity<Proposal> getProposalDetail(@PathVariable Long id) {
        return ResponseEntity.ok(proposalService.getProposalDetail(id));
    }

    // [추가됨] 요청 데이터를 받기 위한 DTO (내부 클래스)
    @Data
    public static class CreateProposalRequest {
        private Long userId;     // 작성자 ID (임시)
        private Long noticeId;   // 공고 ID
        private Long templateId; // 템플릿 ID
        private String title;    // 제안서 제목
    }
}