package com.example.agent_rnd.domain.artifact;

import com.example.agent_rnd.domain.enums.ArtifactType;
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
@Table(name = "ARTIFACTS")
public class Artifact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artifact_id")
    private Long id;

    // 발표자료와 연결 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentation_id", nullable = false)
    private Presentation presentation;

    // Enums 패키지 사용
    @Enumerated(EnumType.STRING)
    @Column(name = "artifact_type", nullable = false, length = 20)
    private ArtifactType artifactType; // PPT or SCRIPT_FILE

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl; // S3 URL

    @Column(name = "file_name")
    private String fileName;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Artifact(Presentation presentation, ArtifactType artifactType, String fileUrl, String fileName) {
        this.presentation = presentation;
        this.artifactType = artifactType;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
    }
}