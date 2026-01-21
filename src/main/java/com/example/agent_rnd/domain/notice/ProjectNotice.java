package com.example.agent_rnd.domain.notice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "PROJECT_NOTICES")
public class ProjectNotice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(name = "seq", length = 100)
    private String seq;

    @Column(name = "title", length = 500, nullable = false)
    private String title;

    @Column(name = "link", length = 1000)
    private String url;

    @Column(name = "author", length = 100)
    private String author;

    // [중요] DB 컬럼명이 CamelCase
    @Column(name = "excInsttNm", length = 100)
    private String agency;

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    // [중요] DB 컬럼명이 CamelCase
    @Column(name = "pubDate", length = 50)
    private String pubDate;

    // [중요] DB 컬럼명이 CamelCase
    @Column(name = "reqstDt", length = 100)
    private String requestPeriod;

    // [중요] DB 컬럼명이 CamelCase
    @Column(name = "trgetNm", length = 200)
    private String targetName;

    // [중요] DB 컬럼명이 CamelCase
    @Column(name = "printFlpthNm", columnDefinition = "TEXT")
    private String filePath;

    // [중요] DB 컬럼명이 CamelCase
    @Column(name = "printFileNm", columnDefinition = "TEXT")
    private String fileName;

    // [중요] DB 컬럼명이 CamelCase
    @Column(name = "hashTags", columnDefinition = "TEXT")
    private String hashTags;
}