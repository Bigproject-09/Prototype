package com.example.agent_rnd.domain.plan;

import com.example.agent_rnd.domain.enums.PlanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Integer planId;

    @Column(name = "plan_name", nullable = false, length = 50)
    private String planName;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_downloadable", nullable = false)
    private Boolean isDownloadable;

    // [추가] DB에 새로 만든 컬럼과 연결
    @Column(name = "plan_type", length = 20)
    private String planType;

}
