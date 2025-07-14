package com.lucassilvs.test.oauth2testexample.application;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class WebTestEntrypoint {

    @GetMapping("/teste")
    public Map<String, Object> teste() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint de teste funcionando!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", "success");
        return response;
    }

    @GetMapping("/teste-oauth2")
    @PreAuthorize("hasAuthority('SCOPE_read')")
    public Map<String, Object> testeOAuth2(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint OAuth2 funcionando!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", "authenticated");
        response.put("subject", jwt.getSubject());
        response.put("scopes", jwt.getClaimAsStringList("scope"));
        return response;
    }
}
