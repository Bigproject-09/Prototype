package com.example.agent_rnd.domain.user;

import com.example.agent_rnd.domain.company.Company;
import com.example.agent_rnd.domain.enums.UserRole;
import com.example.agent_rnd.domain.payment.Payment;
import com.example.agent_rnd.domain.plan.Plan;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private User parent;

    @OneToMany(mappedBy = "parent")
    private List<User> children = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Payment> payments = new ArrayList<>();

    private User(Company company, Plan plan, String email, String password, UserRole role) {
        this.company = company;
        this.plan = plan;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static User createMaster(Company company, Plan plan, String email, String password) {
        return new User(company, plan, email, password, UserRole.MASTER);
    }

    public static User createAdmin(Company company, Plan plan, String email, String password, User parent) {
        User u = new User(company, plan, email, password, UserRole.ADMIN);
        u.parent = parent;
        return u;
    }

    public static User createMember(Company company, Plan plan, String email, String password, User parent) {
        User u = new User(company, plan, email, password, UserRole.MEMBER);
        u.parent = parent;
        return u;
    }

    public void changePlan(Plan plan) {
        this.plan = plan;
    }
}
