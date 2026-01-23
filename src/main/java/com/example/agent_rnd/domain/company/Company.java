package com.example.agent_rnd.domain.company;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMPANIES")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "business_reg_no", nullable = false, length = 20, unique = true)
    private String businessRegNo;

    @Column(name = "contract_status", nullable = false, length = 20)
    private String contractStatus; // ACTIVE / EXPIRED

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    protected Company() {}

    public static Company create(String companyName, String businessRegNo,
                                 LocalDateTime startDate, LocalDateTime endDate) {
        Company c = new Company();
        c.companyName = companyName;
        c.businessRegNo = businessRegNo;
        c.contractStatus = "ACTIVE";
        c.startDate = startDate;
        c.endDate = endDate;
        return c;
    }
}
