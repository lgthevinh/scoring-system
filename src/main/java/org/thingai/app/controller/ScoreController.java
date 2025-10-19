package org.thingai.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thingai.app.scoringservice.ScoringService;
import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.entity.score.Score;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/score")
public class ScoreController {
    /**
     * Retrieves the current score for a specific alliance.
     * @param allianceId The unique ID of the alliance (e.g., "Q1_R").
     * @return A ResponseEntity containing the Score object or an error.
     */
    @GetMapping("/alliance/{allianceId}")
    public ResponseEntity<Object> getScore(@PathVariable String allianceId) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.scoreHandler().getScoreByAllianceId(allianceId, new RequestCallback<String>() {
            @Override
            public void onSuccess(String responseObject, String message) {
                future.complete(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseObject));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(createErrorResponse(errorCode, errorMessage));
            }
        });
        return getObjectResponse(future);
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<Object> getMatchScores(@PathVariable String matchId) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.scoreHandler().getScoresByMatchId(matchId, new RequestCallback<String>() {
            @Override
            public void onSuccess(String responseObject, String message) {
                future.complete(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseObject));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(createErrorResponse(errorCode, errorMessage));
            }
        });
        return getObjectResponse(future);
    }

    /**
     * Submits raw scoring data for an alliance, triggers calculation, and saves the result.
     * @param allianceId The unique ID of the alliance to score.
     * @param scoreDetailsDto The JSON body representing the raw score details.
     * @return A ResponseEntity containing the final calculated Score object or an error.
     */
    @PostMapping("/submit/{allianceId}")
    public ResponseEntity<Object> submitScore(@PathVariable String allianceId, @RequestBody Object scoreDetailsDto) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.scoreHandler().submitScore(allianceId, scoreDetailsDto, false, new RequestCallback<Score>() {
            @Override
            public void onSuccess(Score score, String message) {
                future.complete(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(score));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(createErrorResponse(errorCode, errorMessage));
            }
        });
        return getObjectResponse(future);
    }

    // --- Helper Methods ---

    private ResponseEntity<Object> createErrorResponse(int errorCode, String errorMessage) {
        Map<String, Object> body = Map.of("errorCode", errorCode, "error", errorMessage);
        HttpStatus status = switch (errorCode) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 404 -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity.status(status).body(body);
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
