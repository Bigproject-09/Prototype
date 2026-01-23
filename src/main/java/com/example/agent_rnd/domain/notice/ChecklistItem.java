package com.example.agent_rnd.domain.notice;

import jakarta.persistence.*;
import lombok.*;
import com.example.agent_rnd.domain.enums.ChecklistType;

@Entity
@Table(name = "CHECKLISTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private ProjectNotice notice;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ChecklistType type;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
