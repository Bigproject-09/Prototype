package com.example.agent_rnd.service;

import com.example.agent_rnd.domain.proposal.Proposal;
import com.example.agent_rnd.dto.ProposalRequest;
import com.example.agent_rnd.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final RestTemplate restTemplate;

    // 파이썬 서버 주소 (데이터 팀과 협의 필요)
    private final String PYTHON_SERVER_URL = "http://localhost:5000/parse";

    @Transactional
    public Long processProposalFile(MultipartFile file, ProposalRequest request) {

        // 1. 파일 확장자 검사 (DOCX, PDF만 허용)
        validateFileExtension(file);

        // 2. 파이썬 서버로 파일 전송 -> JSON 결과 받기
        String parsedJson = sendFileToPythonServer(file);

        // 3. 결과 DB 저장
        Proposal proposal = Proposal.builder()
                .noticeId(request.getNoticeId())
                .userId(request.getUserId())
                .title(request.getTitle())
                .fileName(file.getOriginalFilename())
                .parsedJson(parsedJson)
                .build();

        return proposalRepository.save(proposal).getId();
    }

    // 확장자 유효성 검사 메서드
    private void validateFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (!StringUtils.hasText(filename)) {
            throw new IllegalArgumentException("파일 이름이 유효하지 않습니다.");
        }

        String extension = StringUtils.getFilenameExtension(filename);
        if (extension == null) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }

        // 허용할 확장자 목록
        List<String> allowedExtensions = Arrays.asList("pdf", "docx", "doc");

        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (pdf, docx만 가능)");
        }
    }

    // 파이썬 통신 메서드
    private String sendFileToPythonServer(MultipartFile file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    PYTHON_SERVER_URL,
                    requestEntity,
                    String.class
            );

            return response.getBody();

        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 실패", e);
        } catch (Exception e) {
            // 파이썬 서버가 없을 때 테스트를 위한 더미 데이터 (개발용)
            System.out.println("파이썬 서버 연결 실패. 더미 데이터를 저장합니다.");
            return "{\"summary\": \"파이썬 서버 연결 실패 - 테스트용 더미 JSON\", \"pages\": []}";
        }
    }

    public Proposal getProposal(Long proposalId) {
        return proposalRepository.findById(proposalId)
                .orElseThrow(() -> new IllegalArgumentException("제안서가 없습니다."));
    }
}