package org.thingai.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thingai.app.scoringservice.ScoringService;
import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.entity.team.Team;
import org.thingai.app.scoringservice.handler.TeamHandler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    // Get the singleton handler instance from the ScoringService
    private final TeamHandler teamHandler = ScoringService.teamHandler();

    @PostMapping("/create")
    public ResponseEntity<Object> createTeam(@RequestBody Map<String, Object> requestBody) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();

        try {
            String teamId = requestBody.get("teamId").toString();
            String teamName = requestBody.get("teamName").toString();
            String teamSchool = requestBody.get("teamSchool").toString();
            String teamRegion = requestBody.get("teamRegion").toString();

            teamHandler.addTeam(teamId, teamName, teamSchool, teamRegion, new RequestCallback<Team>() {
                @Override
                public void onSuccess(Team team, String message) {
                    future.complete(ResponseEntity.status(HttpStatus.CREATED).body(team));
                }

                @Override
                public void onFailure(int errorCode, String errorMessage) {
                    future.complete(createErrorResponse(errorCode, errorMessage));
                }
            });
        } catch (Exception e) {
            future.complete(ResponseEntity.badRequest().body(Map.of("error", "Invalid request format: " + e.getMessage())));
        }
        return getObjectResponse(future);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> listTeams() {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        teamHandler.listTeams(new RequestCallback<Team[]>() {
            @Override
            public void onSuccess(Team[] teams, String message) {
                future.complete(ResponseEntity.ok(teams));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(createErrorResponse(errorCode, errorMessage));
            }
        });
        return getObjectResponse(future);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTeam(@PathVariable String id) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        teamHandler.getTeamById(id, new RequestCallback<Team>() {
            @Override
            public void onSuccess(Team team, String message) {
                future.complete(ResponseEntity.ok(team));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(createErrorResponse(errorCode, errorMessage));
            }
        });
        return getObjectResponse(future);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateTeam(@RequestBody Team team) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        teamHandler.updateTeam(team, new RequestCallback<Team>() {
            @Override
            public void onSuccess(Team updatedTeam, String message) {
                future.complete(ResponseEntity.ok(updatedTeam));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(createErrorResponse(errorCode, errorMessage));
            }
        });
        return getObjectResponse(future);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteTeam(@PathVariable String id) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        teamHandler.deleteTeam(id, new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void result, String message) {
                future.complete(ResponseEntity.ok(Map.of("message", message)));
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
