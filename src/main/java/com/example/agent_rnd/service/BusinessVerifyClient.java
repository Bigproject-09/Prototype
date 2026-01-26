package com.example.agent_rnd.service;

import com.example.agent_rnd.config.ExternalDataGoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BusinessVerifyClient {

    private final ExternalDataGoProperties props;
    private final RestClient restClient = RestClient.builder().build();

    public record ValidationApiRequest(List<BusinessDescription> businesses) {}
    public record BusinessDescription(
            String b_no,
            String start_dt,
            String p_nm,
            String p_nm2,
            String b_nm,
            String corp_no,
            String b_sector,
            String b_type,
            String b_adr
    ) {}

    public record ValidationApiResponse(
            String status_code,
            Integer request_cnt,
            Integer valid_cnt,
            List<BusinessValidation> data
    ) {}

    public record BusinessValidation(String b_no, String valid, String valid_msg) {}

    public ValidationApiResponse validate(String businessRegNo10, String openDateYYYYMMDD, String ceoName) {
        String url = props.baseUrl()
                + "/validate"
                + "?serviceKey=" + props.serviceKey()
                + "&returnType=JSON";

        ValidationApiRequest body = new ValidationApiRequest(
                List.of(new BusinessDescription(
                        businessRegNo10, openDateYYYYMMDD, ceoName,
                        "", "", "", "", "", ""
                ))
        );

        return restClient.post()
                .uri(URI.create(url))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(ValidationApiResponse.class);
    }
}