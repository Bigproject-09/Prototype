package com.example.agent_rnd.domain.notice;

import com.example.agent_rnd.domain.enums.ReferenceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "NOTICE_REFERENCES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private ProjectNotice notice;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ReferenceType type;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;
}
