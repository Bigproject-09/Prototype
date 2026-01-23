package com.example.agent_rnd.repository;

import com.example.agent_rnd.domain.presentation.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {
}