package com.example.agent_rnd.domain.notice;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notice_files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private ProjectNotice notice;

    @Column(name = "print_file_nm", nullable = false, length = 200)
    private String printFileNm;

    @Column(name = "print_flpth_nm", nullable = false, length = 500)
    private String printFlpthNm;

    @OneToOne(mappedBy = "file", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private NoticeAttachment attachment;

    private NoticeFile(ProjectNotice notice, String printFileNm, String printFlpthNm) {
        this.notice = notice;
        this.printFileNm = printFileNm;
        this.printFlpthNm = printFlpthNm;
    }

    public static NoticeFile create(ProjectNotice notice, String printFileNm, String printFlpthNm) {
        return new NoticeFile(notice, printFileNm, printFlpthNm);
    }

    public void attach(NoticeAttachment attachment) {
        this.attachment = attachment;
        if (attachment != null) {
            attachment.setFile(this);
        }
    }
}
