package com.example.agent_rnd.domain.proposal;

public enum ProposalStatus {
    WRITING,    // [추가] 작성 중 (AI 생성 중 포함)
    DRAFT,      // 초안 완료
    COMPLETED,  // 최종 완료
    FAILED      // 실패
}