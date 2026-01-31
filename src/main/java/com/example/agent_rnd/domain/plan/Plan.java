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

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false, length = 20)
    private PlanType planType;

    /**
     * ✅ DB 컬럼이 없는 파생 값.
     * 기존 API 응답에서 previewPage를 유지해야 할 때 사용.
     * - FREE: 미리보기 3페이지
     * - PAID: 제한 없음(null)
     */
    @Transient
    public Integer getPreviewPage() {
        if (planType == null) return null;
        return (planType == PlanType.FREE) ? 3 : null;
    }
}
