package com.example.agent_rnd.service;

import com.example.agent_rnd.domain.company.Company;
import com.example.agent_rnd.domain.company.ContractStatus;
import com.example.agent_rnd.domain.plan.Plan;
import com.example.agent_rnd.domain.user.User;
import com.example.agent_rnd.domain.user.UserRole;
import com.example.agent_rnd.domain.user.UserStatus;
import com.example.agent_rnd.repository.*;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PlanRepository planRepository;
    private final DraftRepository draftRepository;
    private final ProposalRepository proposalRepository;
    //private final PasswordEncoder passwordEncoder;

    // =========================
    // 기존 기능: 이메일 중복 체크
    // =========================
    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    // =========================
    // 기존 기능: ID로 회원 조회
    // =========================
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));
    }

    // =========================================================
    // 4) 1단계 회원가입: 회사 생성 + 관리자 1명 생성 (관리자 발급 방식)
    // =========================================================
    @Transactional
    public SignupResult companySignupAndCreateAdmin(
            String companyName,
            String businessRegNo,
            String adminEmail,
            String password,
            String passwordConfirm,
            Integer planId
    ) {
        // 1) 회사 사업자번호 중복 방지
        if (companyRepository.findByBusinessRegNo(businessRegNo).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 사업자등록번호입니다.");
        }

        // 2) 관리자 이메일 중복 방지
        if (userRepository.existsByEmail(adminEmail)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 3) 비밀번호 확인
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }

        // 4) Plan 필수 (users.plan_id NOT NULL)
        int resolvedPlanId = (planId == null) ? 1 : planId;
        Plan plan = planRepository.findById(resolvedPlanId)
                .orElseThrow(() -> new IllegalArgumentException("플랜이 존재하지 않습니다. planId=" + resolvedPlanId));

        // 5) company_id 채번 (AUTO_INCREMENT 없음)
        Long nextCompanyId = companyRepository.findMaxId() + 1;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusYears(1);

        Company company = new Company(
                nextCompanyId,
                companyName,
                businessRegNo,
                ContractStatus.PENDING,
                now,
                end
        );
        companyRepository.save(company);

        // 6) user_id 채번 (AUTO_INCREMENT 없음)
        Long nextUserId = userRepository.findMaxId() + 1;

        // 7) 비밀번호 저장 (지금은 SHA-256로 임시, 나중에 BCrypt로 교체)
        String encoded = sha256(password);

        User admin = new User(
                nextUserId,
                company,
                plan,
                adminEmail,
                encoded,
                UserRole.ADMIN,
                UserStatus.ACTIVE,
                now,
                false
        );
        userRepository.save(admin);

        return new SignupResult(company.getId(), admin.getId());
    }

    // ============================================
    // 4) 1단계: 관리자 -> 사용자 여러 명 생성
    // ============================================
    @Transactional
    public List<CreatedUser> adminCreateUsers(
            Long companyId,
            Integer planId,
            List<CreateUserParam> users
    ) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사 정보가 없습니다. companyId=" + companyId));

        int resolvedPlanId = (planId == null) ? 1 : planId;
        Plan plan = planRepository.findById(resolvedPlanId)
                .orElseThrow(() -> new IllegalArgumentException("플랜이 존재하지 않습니다. planId=" + resolvedPlanId));

        LocalDateTime now = LocalDateTime.now();
        List<CreatedUser> result = new ArrayList<>();

        for (CreateUserParam u : users) {
            if (u.email() == null || u.email().isBlank()) {
                throw new IllegalArgumentException("이메일은 필수입니다.");
            }
            if (u.role() == null) {
                throw new IllegalArgumentException("role은 필수입니다.");
            }

            if (userRepository.existsByEmail(u.email())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + u.email());
            }

            Long nextUserId = userRepository.findMaxId() + 1;
            String tempPassword = randomTempPassword();

            User user = new User(
                    nextUserId,
                    company,
                    plan,
                    u.email(),
                    //passwordEncoder.encode(tempPassword),
                    sha256(tempPassword),
                    u.role(),
                    UserStatus.ACTIVE,
                    now,
                    false
            );

            userRepository.save(user);
            result.add(new CreatedUser(user.getId(), user.getEmail(), tempPassword));
        }

        return result;
    }

    // ============================================
    // 5) 관리자 -> 사용자 삭제
    // ============================================
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }


    // -------- 내부 유틸 --------
    private String randomTempPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    // -------- 반환/입력용 record --------
    public record SignupResult(Long companyId, Long adminUserId) {}

    public record CreateUserParam(String email, UserRole role) {}

    public record CreatedUser(Long userId, String email, String tempPassword) {}

    private String sha256(String raw) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
