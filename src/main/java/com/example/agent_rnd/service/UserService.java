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

    private final BusinessVerifyClient businessVerifyClient;
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
            String openDate,
            String ceoName,
            String adminEmail,
            String password,
            String passwordConfirm,
            Integer planId
    ) {
        // 0) 입력 정리 (국세청 요구사항: b_no=10자리 숫자, start_dt=YYYYMMDD) :contentReference[oaicite:3]{index=3}
        String bno = businessRegNo == null ? "" : businessRegNo.replaceAll("[^0-9]", "");
        String startDt = openDate == null ? "" : openDate.replaceAll("[^0-9]", ""); // "2026-01-20" -> "20260120"
        String pnm = ceoName == null ? "" : ceoName.trim();

        if (bno.length() != 10) throw new IllegalArgumentException("사업자등록번호는 숫자 10자리여야 합니다. '-' 제거해서 보내세요.");
        if (startDt.length() != 8) throw new IllegalArgumentException("개업일자는 YYYYMMDD 형식이어야 합니다.");
        if (pnm.isBlank()) throw new IllegalArgumentException("대표자명(ceoName)은 필수입니다.");

        // 1) (A안) 여기서 진위확인 먼저
        var verifyRes = businessVerifyClient.validate(bno, startDt, pnm);

        if (verifyRes == null || verifyRes.data() == null || verifyRes.data().isEmpty()) {
            throw new IllegalArgumentException("사업자 진위확인 응답이 비정상입니다.");
        }
        var item = verifyRes.data().get(0);
        if (!"01".equals(item.valid())) {
            // valid=02면 "확인할 수 없습니다"가 올 수 있음 :contentReference[oaicite:4]{index=4}
            throw new IllegalArgumentException("사업자 진위확인 실패: " + (item.valid_msg() == null ? "" : item.valid_msg()));
        }

        // 2) 회사 사업자번호 중복 방지
        if (companyRepository.findByBusinessRegNo(bno).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 사업자등록번호입니다.");
        }

        // 3) 관리자 이메일 중복 방지
        if (userRepository.existsByEmail(adminEmail)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 4) 비밀번호 확인
        if (password == null || password.isBlank()) throw new IllegalArgumentException("비밀번호는 필수입니다.");
        if (!password.equals(passwordConfirm)) throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");

        // 5) Plan
        int resolvedPlanId = (planId == null) ? 1 : planId;
        Plan plan = planRepository.findById(resolvedPlanId)
                .orElseThrow(() -> new IllegalArgumentException("플랜이 존재하지 않습니다. planId=" + resolvedPlanId));

        // 6) company/user 생성(기존 로직 그대로)
        Long nextCompanyId = companyRepository.findMaxId() + 1;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusYears(1);

        Company company = new Company(
                nextCompanyId,
                companyName,
                bno,
                ContractStatus.PENDING,
                now,
                end
        );
        companyRepository.save(company);

        Long nextUserId = userRepository.findMaxId() + 1;
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
