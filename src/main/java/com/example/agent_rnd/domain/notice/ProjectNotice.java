package com.example.agent_rnd.domain.notice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "project_notices") // [수정] SQL 스크립트의 소문자 테이블명 반영
public class ProjectNotice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "link", columnDefinition = "TEXT")
    private String url;

    @Column(name = "seq", length = 50)
    private String seq;

    @Column(name = "author")
    private String author;

    // [확정] CamelCase 유지
    @Column(name = "excInsttNm")
    private String agency;

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    // [확정] CamelCase 유지
    @Column(name = "pubDate", length = 100)
    private String pubDate;

    // [확정] CamelCase 유지
    @Column(name = "reqstDt")
    private String requestPeriod;

    // [확정] CamelCase 유지
    @Column(name = "trgetNm", columnDefinition = "TEXT")
    private String targetName;

    // [확정] CamelCase 유지
    @Column(name = "printFlpthNm", columnDefinition = "TEXT")
    private String filePath;

    // [확정] CamelCase 유지
    @Column(name = "printFileNm", columnDefinition = "TEXT")
    private String fileName;

    // [확정] CamelCase 유지
    @Column(name = "hashTags", columnDefinition = "TEXT")
    private String hashTags;
}