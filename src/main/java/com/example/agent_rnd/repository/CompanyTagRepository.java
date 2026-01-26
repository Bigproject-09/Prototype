package com.example.agent_rnd.repository;

import com.example.agent_rnd.domain.company.Company;
import com.example.agent_rnd.domain.company.CompanyTag;
import com.example.agent_rnd.domain.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyTagRepository extends JpaRepository<CompanyTag, Long> {

    List<CompanyTag> findByCompany(Company company);

    List<CompanyTag> findByTag(Tag tag);

    boolean existsByCompanyAndTag(Company company, Tag tag);

    void deleteByCompany_CompanyId(Long companyId);
}
