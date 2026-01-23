package com.example.agent_rnd.domain.script;

import com.example.agent_rnd.domain.presentation.Presentation;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SCRIPTS")
public class Script {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "script_id")
    private Long id;

    // 발표자료와 연결 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentation_id", nullable = false)
    private Presentation presentation;

    @Column(name = "page_no", nullable = false)
    private Integer pageNo;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content; // 대본 내용

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Script(Presentation presentation, Integer pageNo, String content) {
        this.presentation = presentation;
        this.pageNo = pageNo;
        this.content = content;
    }
}