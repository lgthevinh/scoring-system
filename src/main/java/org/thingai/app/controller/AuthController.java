package org.thingai.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thingai.app.scoringservice.ScoringService;
import org.thingai.app.scoringservice.handler.systembase.AuthHandler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> request, HttpServletRequest servletRequest) {
        String username = request.get("username");
        String password = request.get("password");

        // --- LOCAL LOGIN IMPLEMENTATION ---
        String remoteAddr = servletRequest.getRemoteAddr();
        boolean isLocalhost = "127.0.0.1".equals(remoteAddr) || "0:0:0:0:0:0:0:1".equals(remoteAddr);

        if ("local".equalsIgnoreCase(username) && isLocalhost) {
            // Bypass normal authentication for local development.
            // This is a convenient backdoor for testing on a local machine.
            String token = ScoringService.authHandler().generateTokenForLocalUser();
            return ResponseEntity.ok(Map.of("token", token, "message", "Local login successful."));
        }
        // --- END LOCAL LOGIN IMPLEMENTATION ---

        // Standard authentication flow, now made thread-safe.
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();

        ScoringService.authHandler().handleAuthenticate(username, password, new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String token, String successMessage) {
                future.complete(ResponseEntity.ok(Map.of("token", token, "message", successMessage)));
            }

            @Override
            public void onFailure(String errorMessage) {
                future.complete(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", errorMessage)));
            }
        });

        return getObjectResponse(future);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@RequestHeader Map<String, String> requestHeaders) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        String authHeader = requestHeaders.get("authorization"); // Headers are converted to lowercase

        // Expecting the format "Bearer <token>"
        String token = (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) ? authHeader.substring(7) : null;

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authorization header is missing or malformed."));
        }

        ScoringService.authHandler().handleRefreshToken(token, new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String refreshedToken, String successMessage) {
                future.complete(ResponseEntity.ok(Map.of("token", refreshedToken, "message", successMessage)));
            }

            @Override
            public void onFailure(String errorMessage) {
                future.complete(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", errorMessage)));
            }
        });

        return getObjectResponse(future);
    }

    private ResponseEntity<Object> getObjectResponse(CompletableFuture<ResponseEntity<Object>> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }
}
