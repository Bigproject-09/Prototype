package com.example.agent_rnd.service;

import com.example.agent_rnd.dto.AuthDtos;
import com.example.agent_rnd.domain.company.Company;
import com.example.agent_rnd.domain.plan.Plan;
import com.example.agent_rnd.domain.user.User;
import com.example.agent_rnd.repository.CompanyRepository;
import com.example.agent_rnd.repository.CompanyTagRepository;
import com.example.agent_rnd.repository.PlanRepository;
import com.example.agent_rnd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyTagRepository companyTagRepository;
    private final PlanRepository planRepository;
    private final BusinessVerifyClient businessVerifyClient;
    private final EmailAuthService emailAuthService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public SignupResult companySignupAndCreateAdmin(AuthDtos.CompanySignupRequest req) {

        String bno = normalizeDigits(req.businessRegNo());
        String startDt = normalizeDigits(req.openDate());
        String pnm = (req.ceoName() == null) ? "" : req.ceoName().trim();

        if (bno.length() != 10) throw new IllegalArgumentException("사업자등록번호는 숫자 10자리여야 합니다.");
        if (startDt.length() != 8) throw new IllegalArgumentException("개업일자는 YYYYMMDD 형식이어야 합니다.");
        if (pnm.isBlank()) throw new IllegalArgumentException("대표자명은 필수입니다.");

        if (companyRepository.existsByBusinessRegNo(bno)) {
            throw new IllegalArgumentException("이미 등록된 사업자등록번호입니다.");
        }
        if (!emailAuthService.isVerified(req.email())) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (req.password() == null || req.password().isBlank()) throw new IllegalArgumentException("비밀번호는 필수입니다.");
        if (!req.password().equals(req.passwordConfirm())) throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");

        var verifyRes = businessVerifyClient.validate(bno, startDt, pnm);
        if (verifyRes == null || verifyRes.data() == null || verifyRes.data().isEmpty()) {
            throw new IllegalArgumentException("사업자 진위확인 응답이 비정상입니다.");
        }
        var item = verifyRes.data().get(0);
        if (!"01".equals(item.valid())) {
            throw new IllegalArgumentException("사업자 진위확인 실패: " + item.valid_msg());
        }

        int resolvedPlanId = (req.planId() == null) ? 1 : req.planId();
        Plan plan = planRepository.findById(resolvedPlanId)
                .orElseThrow(() -> new IllegalArgumentException("플랜이 존재하지 않습니다."));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusYears(1);

        Company company = Company.create(req.companyName(), bno, now, end);
        companyRepository.save(company);

        String encoded = passwordEncoder.encode(req.password());
        User admin = User.createMaster(company, plan, req.email(), encoded);
        userRepository.save(admin);

        return new SignupResult(company.getCompanyId(), admin.getUserId());
    }

    @Transactional
    public void deleteCompanySignup(Long companyId) {
        // 1) 회사 존재 확인
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사가 없습니다."));

        // 2) 회사 유저(1명) 찾기
        User user = userRepository.findFirstByCompany_CompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회사의 사용자가 없습니다."));

        Long userId = user.getUserId();

        // 3) (선택) user_id를 참조하는 테이블 먼저 삭제 필요할 수 있음
        // 예: noticeAttachmentRepository.deleteByUser_UserId(userId);
        // 예: proposalRepository.deleteByUserId(userId);  (지금 Proposal은 FK가 아니라 단순 컬럼이라 정책에 따라)

        // 4) 회사 태그 매핑 삭제(회사 FK 때문에 회사 삭제 전 먼저)
        companyTagRepository.deleteByCompany_CompanyId(companyId);

        // 5) 유저 삭제(유저가 company FK를 들고 있으니 회사 삭제 전)
        userRepository.deleteById(userId);

        // 6) 회사 삭제
        companyRepository.delete(company);
    }

    private String normalizeDigits(String s) {
        return (s == null) ? "" : s.replaceAll("[^0-9]", "");
    }

    public record SignupResult(Long companyId, Long adminUserId) {}
}
