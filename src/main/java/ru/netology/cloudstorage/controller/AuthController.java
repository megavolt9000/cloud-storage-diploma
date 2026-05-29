package ru.netology.cloudstorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstorage.dto.LoginRequest;
import ru.netology.cloudstorage.dto.LoginResponse;
import ru.netology.cloudstorage.service.AuthService;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(

            @RequestBody
            LoginRequest request
    ) {

        String token = authService.login(
                request.getLogin(),
                request.getPassword()
        );

        LoginResponse response =
                LoginResponse.builder()

                        .authToken(token)

                        .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(

            @RequestHeader("auth-token")
            String authToken
    ) {

        authService.logout(authToken);

        return ResponseEntity.ok().build();
    }
}
