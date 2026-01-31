package com.example.agent_rnd.domain.notice;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notice_hashtags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long hashtagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private ProjectNotice notice;

    @Column(name = "tag_name", nullable = false, length = 50)
    private String tagName;

    private NoticeHashtag(ProjectNotice notice, String tagName) {
        this.notice = notice;
        this.tagName = tagName;
    }

    public static NoticeHashtag create(ProjectNotice notice, String tagName) {
        return new NoticeHashtag(notice, tagName);
    }
}
