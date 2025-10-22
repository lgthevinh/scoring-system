package org.thingai.app.controller.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ResponseEntityUtil {

    public static synchronized ResponseEntity<Object> getObjectResponse(CompletableFuture<ResponseEntity<Object>> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    public static synchronized ResponseEntity<Object> createErrorResponse(int errorCode, String errorMessage) {
        Map<String, Object> body = Map.of("errorCode", errorCode, "error", errorMessage);
        HttpStatus status = switch (errorCode) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 404 -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity.status(status).body(body);
    }
}
