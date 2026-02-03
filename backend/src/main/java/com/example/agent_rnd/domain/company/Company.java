package com.example.agent_rnd.domain.company;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import com.example.agent_rnd.domain.enums.ContractStatus;

@Entity
@Getter
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_status", nullable = false, length = 20)
    private ContractStatus contractStatus;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    public static Company create(String companyName, String businessRegNo, LocalDateTime startDate, LocalDateTime endDate) {
        Company c = new Company();
        c.companyName = companyName;
        c.businessRegNo = businessRegNo;
        c.contractStatus = ContractStatus.PENDING;
        c.startDate = startDate;
        c.endDate = endDate;
        return c;
    }

    public void activate() {
        this.contractStatus = ContractStatus.ACTIVE;
    }
}
