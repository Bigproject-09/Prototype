package com.example.agent_rnd.domain.tag;

import jakarta.persistence.*;

@Entity
@Table(name = "TAGS")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TagCategory category;

    @Column(nullable = false, length = 100)
    private String name;

    protected Tag() {}

    public static Tag create(TagCategory category, String name) {
        Tag t = new Tag();
        t.category = category;
        t.name = name;
        return t;
    }
}
