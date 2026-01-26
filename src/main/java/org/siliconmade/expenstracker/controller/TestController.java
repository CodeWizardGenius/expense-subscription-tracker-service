package org.siliconmade.expenstracker.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/me")
    public String getMyInfo(@AuthenticationPrincipal Jwt principal) {
        // Supabase User ID (UUID)
        String userId = principal.getSubject();

        // Token içindeki e-mail bilgisi (Supabase token claimlerine göre değişebilir)
        String email = principal.getClaim("email");

        return "Giriş yapan kullanıcı ID: " + userId + " - Email: " + email;
    }
}