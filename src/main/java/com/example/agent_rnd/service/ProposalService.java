package com.example.agent_rnd.service;

import com.example.agent_rnd.domain.ai.job.JobStep;
import com.example.agent_rnd.domain.notice.ProjectNotice;
import com.example.agent_rnd.domain.proposal.Proposal;
import com.example.agent_rnd.domain.proposal.ProposalStatus;
import com.example.agent_rnd.domain.template.ProposalTemplate;
import com.example.agent_rnd.domain.user.User;
import com.example.agent_rnd.repository.ProjectNoticeRepository;
import com.example.agent_rnd.repository.ProposalRepository;
import com.example.agent_rnd.repository.ProposalTemplateRepository;
import com.example.agent_rnd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    private final ProjectNoticeRepository projectNoticeRepository;
    private final ProposalTemplateRepository proposalTemplateRepository;
    private final AiJobService aiJobService; // [추가] AI 작업 담당자

    /**
     * 제안서 생성 요청
     * 1. 제안서 엔티티 생성 (WRITING)
     * 2. AI 작업(Job) 등록 (PARSE or GENERATE)
     */
    @Transactional
    public Long createProposal(Long userId, Long noticeId, Long templateId, String title) {
        // 1. 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음 id=" + userId));
        ProjectNotice notice = projectNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공고 없음 id=" + noticeId));
        ProposalTemplate template = proposalTemplateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("템플릿 없음 id=" + templateId));

        // 2. 제안서 저장
        Proposal proposal = Proposal.builder()
                .user(user)
                .projectNotice(notice)
                .template(template)
                .title(title)
                .finalContent("") // 초기엔 빈 값
                .version("v1.0")
                .status(ProposalStatus.WRITING)
                .build();

        Proposal savedProposal = proposalRepository.save(proposal);

        // 3. AI 작업 등록 (여기서는 '공고 분석' 단계부터 시작한다고 가정)
        aiJobService.createJob(savedProposal, JobStep.PARSE);

        return savedProposal.getId();
    }
}