package com.example.agent_rnd.domain.notice;

import com.example.agent_rnd.domain.enums.ChecklistType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "checklists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_id")
    private Long checklistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private ProjectNotice notice;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ChecklistType type;

    @Column(name = "content", nullable = false, length = 500)
    private String content;
}
