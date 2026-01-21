package com.example.agent_rnd.controller.api.proposals;


import com.example.agent_rnd.domain.proposal.Proposal;
import com.example.agent_rnd.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    // 1. 내 제안서 목록 조회
    // 사용법: /api/proposals/my/1 (1번 유저의 제안서)
    // ※ 나중에는 토큰(로그인정보)에서 ID를 꺼내도록 변경될 예정입니다.
    @GetMapping("/my/{userId}")
    public List<Proposal> getMyProposals(@PathVariable Long userId) {
        return proposalService.getMyProposals(userId);
    }

    // 2. 제안서 상세 조회
    // 사용법: /api/proposals/5
    @GetMapping("/{id}")
    public Proposal getProposalDetail(@PathVariable Long id) {
        return proposalService.getProposalDetail(id);
    }
}