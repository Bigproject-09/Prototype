package com.example.agent_rnd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate; // 추가
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import com.example.agent_rnd.config.ExternalDataGoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.RequiredArgsConstructor;

@EnableConfigurationProperties(ExternalDataGoProperties.class)
@ConfigurationPropertiesScan
@EnableJpaAuditing
@SpringBootApplication

@RequiredArgsConstructor

public class AgentRndApplication {

    private final JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {

        SpringApplication.run(AgentRndApplication.class, args);
    }
    @PostConstruct
    public void checkDb() {
        System.out.println("[DB] " +
                jdbcTemplate.queryForObject("SELECT DATABASE()", String.class)
        );

        System.out.println("[DB] " +
                jdbcTemplate.queryForObject(
                        "SELECT CONCAT(@@hostname, ':', @@port)", String.class
                )
        );
    }

    // [추가] 파이썬 서버와 통신할 도구 등록
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
