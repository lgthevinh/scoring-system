package org.thingai.scoringsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thingai.scoringsystem.handler.AuthHandler;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private ResponseEntity<Map<String, String>> responseEntity;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");


        new AuthHandler().handleAuthenticate(username, password, new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String token, String successMessage) {
                responseEntity = ResponseEntity.ok(Map.of("token", token, "message", successMessage));
            }

            @Override
            public void onFailure(String errorMessage) {
                responseEntity = ResponseEntity.status(401).body(Map.of("error", errorMessage));
            }
        });

        return responseEntity;
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader Map<String, String> requestHeaders) {
        String token = requestHeaders.get("Authorization");

        new AuthHandler().handleRefreshToken(token, new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String refreshedToken, String successMessage) {
                responseEntity = ResponseEntity.ok(Map.of("token", refreshedToken, "message", successMessage));
            }

            @Override
            public void onFailure(String errorMessage) {
                responseEntity = ResponseEntity.status(401).body(Map.of("error", errorMessage));
            }
        });

        return responseEntity;
    }
}
