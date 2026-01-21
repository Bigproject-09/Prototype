package com.example.agent_rnd.domain.proposal;

import com.example.agent_rnd.domain.user.User;
import com.example.agent_rnd.domain.notice.ProjectNotice;
import com.example.agent_rnd.domain.template.ProposalTemplate;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PROPOSALS")
public class Proposal {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposal_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private ProjectNotice projectNotice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private ProposalTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(name = "final_content", columnDefinition = "LONGTEXT")
    private String finalContent;

    @Column(length = 20)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProposalStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}