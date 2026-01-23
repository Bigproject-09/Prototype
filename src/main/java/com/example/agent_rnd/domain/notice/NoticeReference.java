package com.example.agent_rnd.domain.notice;

import jakarta.persistence.*;
import lombok.*;
import com.example.agent_rnd.domain.enums.ReferenceType;

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

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    // 링크/파일 경로
    @Column(name = "url", length = 2000)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ReferenceType type;

    // 파일형 참고자료를 운영하면 attachment처럼 filePath/mime/size/checksum 등을 추가 가능
}
