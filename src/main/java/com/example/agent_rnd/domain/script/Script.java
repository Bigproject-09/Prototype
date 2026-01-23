package com.example.agent_rnd.domain.script;

import com.example.agent_rnd.domain.presentation.Presentation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SCRIPTS")
public class Script {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "script_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentation_id", nullable = false)
    private Presentation presentation;

    @Column(name = "page_no", nullable = false)
    private Integer pageNo;

    // 수정: DB 컬럼명(text_content)과 매핑
    @Column(name = "text_content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Builder
    public Script(Presentation presentation, Integer pageNo, String content) {
        this.presentation = presentation;
        this.pageNo = pageNo;
        this.content = content;
    }
}