package com.redis.session.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    private static final String VISITS = "visits";

    @GetMapping("/")
    public Map<String, String> home(HttpSession session) {
        Integer visitCount = (Integer) session.getAttribute(VISITS);
        if (visitCount == null) {
            visitCount = 0;
        }
        session.setAttribute(VISITS, ++visitCount);
        return Map.of(
                "session id", session.getId(),
                VISITS, visitCount.toString()
        );
    }

}
