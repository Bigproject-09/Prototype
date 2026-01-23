package com.example.agent_rnd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate; // 추가
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@EnableJpaAuditing
@SpringBootApplication
public class AgentRndApplication {

    public static void main(String[] args) {

        SpringApplication.run(AgentRndApplication.class, args);
    }

    // [추가] 파이썬 서버와 통신할 도구 등록
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
