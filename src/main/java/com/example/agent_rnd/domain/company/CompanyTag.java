package com.example.agent_rnd.domain.company;

import com.example.agent_rnd.domain.tag.Tag;
import jakarta.persistence.*;

@Entity
@Table(name = "COMPANY_TAGS")
public class CompanyTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "map_id")
    private Long mapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    protected CompanyTag() {}

    public static CompanyTag create(Company company, Tag tag) {
        CompanyTag ct = new CompanyTag();
        ct.company = company;
        ct.tag = tag;
        return ct;
    }
}
