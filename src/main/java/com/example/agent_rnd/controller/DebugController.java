package com.example.agent_rnd.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DebugController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/api/debug/db")
    public Map<String, Object> db() {
        String db = jdbcTemplate.queryForObject("select database()", String.class);
        String url = jdbcTemplate.getDataSource().toString();
        Integer cnt = jdbcTemplate.queryForObject("select count(*) from plans where plan_id=1", Integer.class);
        return Map.of("database", db, "plans_id_1_count", cnt, "datasource", url);
    }
}
