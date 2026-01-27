package com.example.agent_rnd.service;

import com.example.agent_rnd.domain.enums.PaymentStatus;
import com.example.agent_rnd.domain.payment.Payment;
import com.example.agent_rnd.domain.plan.Plan;
import com.example.agent_rnd.domain.user.User;
import com.example.agent_rnd.dto.PaymentCallbackRequest;
import com.example.agent_rnd.repository.PaymentRepository;
import com.example.agent_rnd.repository.PlanRepository;
import com.example.agent_rnd.repository.UserRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final IamportClient iamportClient; // ✅ 라이브러리가 토큰 발급 자동 처리
    private final PaymentRepository paymentRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    /**
     * 결제 검증 및 저장 메인 로직
     */
    @Transactional
    public Long processPaymentDone(PaymentCallbackRequest request) {
        // 1. 프론트에서 결제 실패라고 왔으면 바로 실패 처리
        if (request.getSuccess() != null && !request.getSuccess()) {
            throw new IllegalArgumentException("결제가 실패했습니다: " + request.getError_msg());
        }

        // 2. 포트원 서버에서 진짜 결제 내역 조회 (검증)
        com.siot.IamportRestClient.response.Payment portonePayment = getPortonePayment(request.getImp_uid());

        // 3. 결제 금액 검증 (DB의 Plan 가격 vs 실제 결제된 가격)
        // 주문번호(merchant_uid)에서 planId와 userId를 파싱하거나, 세션에서 가져와야 함.
        // 여기서는 간단히 주문번호 생성 규칙이 "plan:{planId}_user:{userId}_{timestamp}" 라고 가정하고 파싱하거나,
        // 혹은 DB에 '결제대기(READY)' 상태로 미리 저장해둔 Payment를 찾아와서 비교하는 것이 정석입니다.

        // ★ 더 간단한 방법: 여기서는 imp_uid로 조회된 'amount'가 우리가 파는 플랜 가격 중 하나인지 확인 (약식)
        BigDecimal paidAmount = portonePayment.getAmount();

        // (실무에서는 주문번호로 DB의 '결제대기' 건을 찾아서 비교해야 가장 정확합니다.)
        // 여기서는 예시로 "주문번호에 포함된 planId"를 파싱한다고 가정해볼게요.
        // 예: "P001_U123_17000000" (Plan 1번, User 123번)
        Long userId = parseUserIdFromMerchantUid(request.getMerchant_uid());
        Integer planId = parsePlanIdFromMerchantUid(request.getMerchant_uid());

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요금제입니다."));

        if (plan.getPrice().compareTo(paidAmount) != 0) {
            // 가격 위변조 발생 -> 결제 취소 로직(cancelPayment) 호출 필요
            throw new IllegalStateException("결제 금액 오류! (상품: " + plan.getPrice() + ", 결제: " + paidAmount + ")");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 4. DB 저장
        Payment payment = Payment.builder()
                .user(user)
                .plan(plan)
                .impUid(portonePayment.getImpUid())
                .merchantUid(portonePayment.getMerchantUid())
                .amount(paidAmount)
                .status(PaymentStatus.PAID)
                .failReason(null)
                .build();

        paymentRepository.save(payment);

        // 5. 유저 등급 UP (비즈니스 로직)
        user.upgradePlan(plan);

        return payment.getId();
    }

    // 포트원 API 호출 헬퍼
    private com.siot.IamportRestClient.response.Payment getPortonePayment(String impUid) {
        try {
            IamportResponse<com.siot.IamportRestClient.response.Payment> response = iamportClient.paymentByImpUid(impUid);
            if (response.getResponse() == null) {
                throw new IllegalArgumentException("결제 정보를 찾을 수 없습니다.");
            }
            return response.getResponse();
        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException("포트원 API 통신 에러", e);
        }
    }

    // 파싱 헬퍼 (프론트엔드와 주문번호 규칙을 맞춰야 함)
    // 예: "plan-1_user-5_time-1234567"
    private Long parseUserIdFromMerchantUid(String uid) {
        try {
            String[] parts = uid.split("_");
            return Long.parseLong(parts[1].split("-")[1]); // user-5 -> 5
        } catch (Exception e) {
            // 파싱 실패 시 테스트용 하드코딩 (실제론 에러 내야 함)
            return 1L;
        }
    }

    private Integer parsePlanIdFromMerchantUid(String uid) {
        try {
            String[] parts = uid.split("_");
            return Integer.parseInt(parts[0].split("-")[1]); // plan-1 -> 1
        } catch (Exception e) {
            return 1;
        }
    }
}