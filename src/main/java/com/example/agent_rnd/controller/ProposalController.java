package com.example.agent_rnd.controller;

import com.example.agent_rnd.dto.ProposalRequest;
import com.example.agent_rnd.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/proposals")
public class ProposalController {

    private final ProposalService proposalService;

    /**
     * 제안서 저장 API
     * POST /api/proposals
     */
    @PostMapping
    public ResponseEntity<Long> createProposal(@RequestBody ProposalRequest request) {
        Long savedId = proposalService.saveProposal(
                request.getNoticeId(),
                request.getUserId(),
                request.getTitle(),
                request.getFileName(),
                request.getParsedJson()
        );

        return ResponseEntity.ok(savedId);
    }
}