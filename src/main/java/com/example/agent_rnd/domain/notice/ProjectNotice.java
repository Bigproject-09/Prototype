package com.example.agent_rnd.domain.notice;

import jakarta.persistence.*;
import lombok.*;
import com.example.agent_rnd.domain.enums.NoticeStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROJECT_NOTICES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(name = "notice_name", nullable = false, length = 255)
    private String noticeName;

    @Column(name = "organization", length = 255)
    private String organization;

    @Column(name = "budget", length = 255)
    private String budget;

    @Column(name = "period", length = 255)
    private String period;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "url", length = 1000)
    private String url;

    // 해시태그를 문자열로 저장하는 버전(나중에 NOTICE_TAGS 매핑으로 개선 가능)
    @Column(name = "hash_tags", length = 500)
    private String hashTags;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private NoticeStatus status = NoticeStatus.ACTIVE;

    // ====== relations ======

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NoticeAttachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChecklistItem> checklistItems = new ArrayList<>();

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NoticeReference> references = new ArrayList<>();

    // ====== 편의 메서드(양방향 세팅) ======

    public void addAttachment(NoticeAttachment attachment) {
        attachments.add(attachment);
        attachment.setNotice(this);
    }

    public void addChecklistItem(ChecklistItem item) {
        checklistItems.add(item);
        item.setNotice(this);
    }

    public void addReference(NoticeReference ref) {
        references.add(ref);
        ref.setNotice(this);
    }
}
