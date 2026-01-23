package com.example.agent_rnd.domain.user;

import com.example.agent_rnd.domain.company.Company;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_free_used", nullable = false)
    private boolean isFreeUsed;

    protected User() {}

    public static User create(Company company, String email, String password) {
        User u = new User();
        u.company = company;
        u.email = email;
        u.password = password;
        u.createdAt = LocalDateTime.now();
        u.isFreeUsed = false;
        return u;
    }
}
