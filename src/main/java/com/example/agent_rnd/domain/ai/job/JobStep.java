package com.example.agent_rnd.domain.ai.job;

public enum JobStep {
    PARSE,          // 공고/문서 파싱
    REQUIREMENTS,   // 요구사항 도출
    INTERVIEW,      // 인터뷰(질의응답) 생성
    GENERATE,       // 제안서 생성
    REVIEW          // 검토/평가
}