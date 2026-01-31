package com.example.agent_rnd.service;

import com.example.agent_rnd.client.BusinessVerifyClient;
import com.example.agent_rnd.dto.AuthDtos;
import com.example.agent_rnd.domain.company.Company;
import com.example.agent_rnd.domain.enums.UserEntityType;
import com.example.agent_rnd.domain.enums.UserRole;
import com.example.agent_rnd.domain.plan.Plan;
import com.example.agent_rnd.domain.proposal.Proposal;
import com.example.agent_rnd.domain.user.User;
import com.example.agent_rnd.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    private final NoticeAttachmentRepository noticeAttachmentRepository;
    private final ProposalRepository proposalRepository;
    private final PresentationRepository presentationRepository;
    private final ScriptRepository scriptRepository;
    private final PaymentRepository paymentRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // =========================
    // 회사 회원가입 + MASTER 생성
    // =========================
    @Transactional
    public SignupResult companySignupAndCreateMaster(AuthDtos.CompanySignupRequest req) {

        String bno = normalizeDigits(req.businessRegNo());
        String startDt = normalizeDigits(req.openDate());
        String pnm = (req.ceoName() == null) ? "" : req.ceoName().trim();

        if (bno.length() != 10) throw new IllegalArgumentException("사업자등록번호는 숫자 10자리여야 합니다.");
        if (startDt.length() != 8) throw new IllegalArgumentException("개업일자는 YYYYMMDD 형식이어야 합니다.");
        if (pnm.isBlank()) throw new IllegalArgumentException("대표자명은 필수입니다.");

        String email = (req.email() == null) ? "" : req.email().trim().toLowerCase();
        if (email.isBlank()) throw new IllegalArgumentException("이메일은 필수입니다.");

        if (companyRepository.existsByBusinessRegNo(bno)) {
            throw new IllegalArgumentException("이미 등록된 사업자등록번호입니다.");
        }
        if (!emailAuthService.isVerified(email)) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }
        if (userRepository.existsByEmail(email)) {
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

        // tax_type_cd 는 validate 응답 status() 안에 존재
        String taxTypeCd = (item.status() == null) ? null : item.status().tax_type_cd();
        UserEntityType userEntityType = UserEntityType.fromTaxTypeCd(taxTypeCd);

        int resolvedPlanId = (req.planId() == null) ? 1 : req.planId();
        Plan plan = planRepository.findById(resolvedPlanId)
                .orElseThrow(() -> new IllegalArgumentException("플랜이 존재하지 않습니다."));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusYears(1);

        Company company = Company.create(req.companyName(), bno, now, end, userEntityType);
        companyRepository.save(company);

        String encoded = passwordEncoder.encode(req.password());
        User master = User.createMaster(company, plan, email, encoded);
        userRepository.save(master);

        return new SignupResult(company.getCompanyId(), master.getUserId());
    }

    // =========================
    // ✅ 권한별 유저 삭제 (핵심)
    // - MEMBER: 본인만
    // - ADMIN : 본인 + 본인 소속 MEMBER
    // - MASTER: 본인 + 본인 소속 ADMIN + 그 아래 MEMBER
    // =========================
    @Transactional
    public void deleteUserByManager(Long managerUserId, Long targetUserId) {

        User manager = userRepository.findById(managerUserId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다."));

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("대상 유저가 없습니다."));

        // 0) 같은 회사만 가능
        if (!Objects.equals(manager.getCompany().getCompanyId(), target.getCompany().getCompanyId())) {
            throw new AccessDeniedException("다른 회사 유저는 삭제할 수 없습니다.");
        }

        // 1) MEMBER: 자기 자신만
        if (manager.getRole() == UserRole.MEMBER) {
            if (!Objects.equals(manager.getUserId(), targetUserId)) {
                throw new AccessDeniedException("MEMBER는 본인만 삭제할 수 있습니다.");
            }
            hardDeleteUser(targetUserId);
            return;
        }

        // 2) ADMIN: 자기 자신 + 자기 아래 MEMBER만
        if (manager.getRole() == UserRole.ADMIN) {

            // 본인 삭제
            if (Objects.equals(manager.getUserId(), targetUserId)) {
                hardDeleteUser(targetUserId);
                return;
            }

            // MEMBER만 삭제 가능
            if (target.getRole() != UserRole.MEMBER) {
                throw new AccessDeniedException("ADMIN은 MEMBER만 삭제할 수 있습니다.");
            }

            // target.parent == manager 인지 확인
            if (target.getParent() == null || !Objects.equals(target.getParent().getUserId(), manager.getUserId())) {
                throw new AccessDeniedException("ADMIN은 본인에게 속한 MEMBER만 삭제할 수 있습니다.");
            }

            hardDeleteUser(targetUserId);
            return;
        }

        // 3) MASTER: 자기 자신 + 자기 아래 ADMIN + 그 아래 MEMBER
        if (manager.getRole() == UserRole.MASTER) {

            // 본인 삭제 허용(원치 않으면 막아도 됨)
            if (Objects.equals(manager.getUserId(), targetUserId)) {
                hardDeleteUser(targetUserId);
                return;
            }

            // MASTER는 다른 MASTER 삭제 금지(안전장치)
            if (target.getRole() == UserRole.MASTER) {
                throw new AccessDeniedException("MASTER는 다른 MASTER를 삭제할 수 없습니다.");
            }

            // (A) target이 ADMIN이면: target.parent == manager 인지 확인
            if (target.getRole() == UserRole.ADMIN) {
                if (target.getParent() == null || !Objects.equals(target.getParent().getUserId(), manager.getUserId())) {
                    throw new AccessDeniedException("이 MASTER 소속 ADMIN이 아닙니다.");
                }
                // hardDeleteUser가 재귀로 그 아래 MEMBER까지 함께 삭제
                hardDeleteUser(targetUserId);
                return;
            }

            // (B) target이 MEMBER이면: target.parent(ADMIN).parent == manager 인지 확인
            if (target.getRole() == UserRole.MEMBER) {
                if (target.getParent() == null) {
                    throw new AccessDeniedException("소속 ADMIN이 없는 MEMBER는 삭제할 수 없습니다.");
                }

                User parentAdmin = target.getParent();
                if (parentAdmin.getRole() != UserRole.ADMIN) {
                    throw new AccessDeniedException("MEMBER의 부모는 ADMIN이어야 합니다.");
                }

                if (parentAdmin.getParent() == null || !Objects.equals(parentAdmin.getParent().getUserId(), manager.getUserId())) {
                    throw new AccessDeniedException("이 MASTER 라인의 MEMBER가 아닙니다.");
                }

                hardDeleteUser(targetUserId);
                return;
            }

            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        throw new AccessDeniedException("삭제 권한이 없습니다.");
    }

    // =========================
    // 회사 가입 취소(회사 통째 삭제)
    // ⚠️ 주의: 이 메서드는 권한 체크가 없음.
    // 실제 운영에서는 MASTER만 호출 가능하도록 Security/Controller에서 제한하는 게 맞음.
    // =========================
    @Transactional
    public void deleteCompanySignup(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사가 없습니다."));

        userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getCompany().getCompanyId(), companyId) && u.getRole() == UserRole.MEMBER)
                .forEach(u -> hardDeleteUser(u.getUserId()));

        userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getCompany().getCompanyId(), companyId) && u.getRole() == UserRole.ADMIN)
                .forEach(u -> hardDeleteUser(u.getUserId()));

        userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getCompany().getCompanyId(), companyId) && u.getRole() == UserRole.MASTER)
                .forEach(u -> hardDeleteUser(u.getUserId()));

        companyTagRepository.deleteByCompany_CompanyId(companyId);
        companyRepository.delete(company);
    }

    // =========================
    // 실제 강제 삭제 로직(연관 데이터 정리 포함)
    // =========================
    @Transactional
    protected void hardDeleteUser(Long userId) {

        // 1) 자식 먼저 삭제
        userRepository.findByParent_UserId(userId)
                .forEach(child -> hardDeleteUser(child.getUserId()));

        // 2) proposal -> presentation -> script 삭제
        List<Proposal> proposals = proposalRepository.findByUser_UserId(userId);
        for (Proposal p : proposals) {
            Long proposalId = p.getProposalId();

            var presentations = presentationRepository.findByProposal_ProposalId(proposalId);
            for (var pres : presentations) {
                scriptRepository.deleteByPresentation_PresentationId(pres.getPresentationId());
            }

            presentationRepository.deleteByProposal_ProposalId(proposalId);
        }

        // 3) 나머지 FK 데이터 정리
        noticeAttachmentRepository.deleteByUser_UserId(userId);
        proposalRepository.deleteByUser_UserId(userId);
        paymentRepository.deleteByUser_UserId(userId);

        // 4) 유저 삭제
        userRepository.deleteById(userId);
    }

    private String normalizeDigits(String s) {
        return (s == null) ? "" : s.replaceAll("[^0-9]", "");
    }

    public record SignupResult(Long companyId, Long adminUserId) {}
}
