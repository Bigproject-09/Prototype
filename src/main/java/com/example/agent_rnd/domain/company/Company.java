package com.example.agent_rnd.domain.company;

import com.example.agent_rnd.domain.enums.ContractStatus;
import com.example.agent_rnd.domain.enums.UserEntityType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "ceoName", nullable = false, length = 50)
    private String ceoName;

    @Column(name = "openDate", nullable = false)
    private LocalDate openDate;

    @Column(name = "business_reg_no", nullable = false, length = 20)
    private String businessRegNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_status", nullable = false, length = 20)
    private ContractStatus contractStatus;

    // 가입일(계약 시작일)
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    // 로그인 차단일 기준(계약 만료일)
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_entity_type", nullable = false, length = 20)
    private UserEntityType userEntityType;

    // ===== optional (사용자 입력) =====
    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "industry", length = 50)
    private String industry;

    @Column(name = "employees")
    private Long employees;

    @Column(name = "financial_summary", columnDefinition = "json")
    private String financialSummary;

    @Column(name = "history", columnDefinition = "json")
    private String history;

    @Column(name = "core_competency", columnDefinition = "json")
    private String coreCompetency;

    private Company(
            String companyName,
            String ceoName,
            LocalDate openDate,
            String businessRegNo,
            ContractStatus contractStatus,
            LocalDateTime startDate,
            LocalDateTime endDate,
            UserEntityType userEntityType
    ) {
        this.companyName = companyName;
        this.ceoName = ceoName;
        this.openDate = openDate;
        this.businessRegNo = businessRegNo;
        this.contractStatus = contractStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userEntityType = userEntityType;
    }

    public static Company create(
            String companyName,
            String ceoName,
            LocalDate openDate,
            String businessRegNo,
            LocalDateTime startDate,
            LocalDateTime endDate,
            UserEntityType userEntityType
    ) {
        return new Company(
                companyName,
                ceoName,
                openDate,
                businessRegNo,
                ContractStatus.PENDING,
                startDate,
                endDate,
                userEntityType
        );
    }

    public void updateUserEntityType(UserEntityType userEntityType) {
        this.userEntityType = userEntityType;
    }

    public void updateProfile(
            String address,
            String industry,
            Long employees,
            String financialSummaryJson,
            String historyJson,
            String coreCompetencyJson
    ) {
        this.address = address;
        this.industry = industry;
        this.employees = employees;
        this.financialSummary = financialSummaryJson;
        this.history = historyJson;
        this.coreCompetency = coreCompetencyJson;
    }
}
