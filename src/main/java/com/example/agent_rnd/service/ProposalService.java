package com.example.agent_rnd.service;

import com.example.agent_rnd.domain.proposal.Proposal;
import com.example.agent_rnd.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProposalService {

    private final ProposalRepository proposalRepository;

    /**
     * 제안서 저장 (AI 생성의 첫 단계)
     * @param noticeId 공고 ID
     * @param userId 사용자 ID
     * @param title 프로젝트명
     * @param fileName 파일명
     * @param parsedJson 파싱된 JSON 데이터
     * @return 저장된 제안서 ID
     */
    @Transactional
    public Long saveProposal(Long noticeId, Long userId, String title, String fileName, String parsedJson) {

        // 1. 엔티티 생성 (Builder 패턴 사용)
        Proposal proposal = Proposal.builder()
                .noticeId(noticeId)
                .userId(userId)
                .title(title)
                .fileName(fileName)
                .parsedJson(parsedJson)
                .build();

        // 2. 저장 (Repository 이용)
        Proposal savedProposal = proposalRepository.save(proposal);

        // 3. ID 반환
        return savedProposal.getId();
    }

    // 제안서 조회 메서드
    public Proposal getProposal(Long proposalId) {
        return proposalRepository.findById(proposalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 제안서가 존재하지 않습니다. id=" + proposalId));
    }
}