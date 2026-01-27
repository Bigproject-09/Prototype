package com.example.agent_rnd.domain.notice;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROJECT_NOTICES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    // 공고 고유 번호 (외부 시스템 seq)
    @Column(name = "seq", nullable = false, length = 100)
    private String seq;

    // 공고 제목
    @Column(name = "title", nullable = false, length = 500)
    private String title;

    // 공고 링크
    @Column(name = "link", nullable = false, length = 1000)
    private String link;

    // 작성자
    @Column(name = "author", nullable = false, length = 100)
    private String author;

    // 시행 기관명
    @Column(name = "exc_instt_nm", nullable = false, length = 100)
    private String excInsttNm;

    // 공고 설명 (원문)
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    // 게시일
    @Column(name = "pub_date", nullable = false, length = 50)
    private String pubDate;

    // 신청 기간 텍스트
    @Column(name = "reqst_dt", length = 100)
    private String reqstDt;

    // 대상명
    @Column(name = "trget_nm", nullable = false, length = 200)
    private String trgetNm;

    // 출력 파일 경로
    @Column(name = "print_flpth_nm", nullable = false, length = 500)
    private String printFlpthNm;

    // 출력 파일명
    @Column(name = "print_file_nm", nullable = false, length = 200)
    private String printFileNm;

    // 해시태그 문자열
    @Column(name = "hash_tags", nullable = false, length = 500)
    private String hashTags;

    // ===== relations (mappedBy는 "자식의 필드명") =====

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NoticeAttachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChecklistItem> checklistItems = new ArrayList<>();

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NoticeReference> references = new ArrayList<>();

    // ===== 편의 메서드(양방향 세팅) =====

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
