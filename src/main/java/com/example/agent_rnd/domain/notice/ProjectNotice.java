package com.example.agent_rnd.domain.notice;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project_notices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProjectNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @Column(name = "seq", nullable = false, length = 100)
    private String seq;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "link", nullable = false, length = 1000)
    private String link;

    @Column(name = "author", nullable = false, length = 100)
    private String author;

    @Column(name = "exc_instt_nm", nullable = false, length = 100)
    private String excInsttNm;

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "pub_date", nullable = false, length = 50)
    private String pubDate;

    @Column(name = "reqst_dt", length = 100)
    private String reqstDt;

    @Column(name = "trget_nm", nullable = false, length = 200)
    private String trgetNm;

    // ===== relations =====

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NoticeFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NoticeHashtag> hashtags = new ArrayList<>();

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChecklistItem> checklistItems = new ArrayList<>();

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NoticeReference> references = new ArrayList<>();

    // ===== convenience =====

    public void addFile(NoticeFile file) {
        this.files.add(file);
    }

    public void addHashtag(NoticeHashtag hashtag) {
        this.hashtags.add(hashtag);
    }

    public void addChecklistItem(ChecklistItem item) {
        this.checklistItems.add(item);
        item.setNotice(this);
    }

    public void addReference(NoticeReference ref) {
        this.references.add(ref);
        ref.setNotice(this);
    }
}
