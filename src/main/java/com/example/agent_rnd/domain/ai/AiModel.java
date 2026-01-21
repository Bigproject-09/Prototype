package com.example.agent_rnd.domain.ai;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "AI_MODELS")
public class AiModel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long id;

    @Column(name = "model_name", nullable = false, length = 50)
    private String modelName;

    @Column(name = "provider", length = 50)
    private String provider; // OpenAI, Anthropic, HyperCLOVA X 등

    @Column(name = "version", length = 20)
    private String version;
}