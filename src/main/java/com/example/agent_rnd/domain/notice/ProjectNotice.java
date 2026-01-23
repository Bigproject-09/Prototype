package com.example.agent_rnd.domain.notice;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "seq", nullable = false, length = 100)
    private String seq;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "link", nullable = false, length = 1000)
    private String link;

    @Column(name = "author", nullable = false, length = 100)
    private String author;

    @Column(name = "etc_instl_nm", nullable = false, length = 100)
    private String etcInstlNm;

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description; // ERD: NULL 허용

    @Column(name = "pub_date", nullable = false, length = 50)
    private String pubDate;

    @Column(name = "reqst_cl", length = 100)
    private String reqstCl; // ERD: NULL 허용

    @Column(name = "rcrt_clm", nullable = false, length = 200)
    private String rcrtClm;

    @Column(name = "print_lpth_nm", nullable = false, length = 500)
    private String printLpthNm;

    @Column(name = "print_file_nm", nullable = false, length = 200)
    private String printFileNm;

    @Column(name = "hash_tags", nullable = false, length = 500)
    private String hashTags;

    // ===== relations =====

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NoticeAttachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChecklistItem> checklistItems = new ArrayList<>();

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NoticeReference> references = new ArrayList<>();

    // ===== 편의 메서드 =====

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
