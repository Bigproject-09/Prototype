package com.example.agent_rnd.domain.user;

import com.example.agent_rnd.domain.company.Company;
import com.example.agent_rnd.domain.payment.Payment; // ✅ [추가] 결제 내역 연결
import com.example.agent_rnd.domain.plan.Plan;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import com.example.agent_rnd.domain.enums.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private UserRole role; // 0=MASTER, 1=ADMIN, 2=MEMBER

    // 추가] 양방향 매핑: 유저가 결제한 내역들 (조회용)
    // mappedBy = "user"는 Payment 클래스 안의 'private User user;' 필드명을 뜻함
    @OneToMany(mappedBy = "user")
    private List<Payment> payments = new ArrayList<>();

    public static User create(
            Company company,
            Plan plan,
            String email,
            String password,
            UserRole role
    ) {
        User u = new User();
        u.company = company;
        u.plan = plan;
        u.email = email;
        u.password = password;
        u.role = role;
        return u;
    }

    public static User createMaster(Company company, Plan plan, String email, String password) {
        User u = new User();
        u.company = company;
        u.plan = plan;
        u.email = email;
        u.password = password;
        u.role = UserRole.MASTER;
        return u;
    }

    public static User createAdmin(Company company, Plan plan, String email, String password) {
        User u = new User();
        u.company = company;
        u.plan = plan;
        u.email = email;
        u.password = password;
        u.role = UserRole.ADMIN;
        return u;
    }

    public static User createMember(Company company, Plan plan, String email, String password) {
        User u = new User();
        u.company = company;
        u.plan = plan;
        u.email = email;
        u.password = password;
        u.role = UserRole.MEMBER;
        return u;
    }

    // 요금제 변경 메서드 (PaymentService에서 호출함)
    public void upgradePlan(Plan newPlan) {
        this.plan = newPlan;
    }
}