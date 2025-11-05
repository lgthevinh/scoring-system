package org.thingai.app.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thingai.app.controller.utils.ResponseEntityUtil;
import org.thingai.app.scoringservice.ScoringService;
import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.dto.MatchDetailDto;

import java.util.concurrent.CompletableFuture;

import static org.thingai.app.controller.utils.ResponseEntityUtil.createErrorResponse;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    @GetMapping("/playing-matches")
    public ResponseEntity<Object> getCurrentMatches() {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.liveScoreHandler().initialSyncFrontend(new RequestCallback<MatchDetailDto[]>() {
            @Override
            public void onSuccess(MatchDetailDto[] result, String message) {
                future.complete(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(createErrorResponse(errorCode, "Initial sync failed: " + errorMessage));
            }
        });
        return ResponseEntityUtil.getObjectResponse(future);
    }
}
