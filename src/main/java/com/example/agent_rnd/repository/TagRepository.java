package com.example.agent_rnd.repository;

import com.example.agent_rnd.domain.tag.Tag;
import com.example.agent_rnd.domain.enums.TagCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByCategory(TagCategory category);

    boolean existsByCategoryAndName(TagCategory category, String name);
}
