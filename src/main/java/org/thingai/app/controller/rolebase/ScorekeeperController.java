package org.thingai.app.controller.rolebase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thingai.app.controller.utils.ResponseEntityUtil;
import org.thingai.app.scoringservice.ScoringService;
import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.app.scoringservice.entity.score.Score;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/scorekeeper")
public class ScorekeeperController {
    private static final String TAG = "ScorekeeperController";

    @PostMapping("/start-current-match")
    public ResponseEntity<Object> startMatch() {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.liveScoreHandler().startCurrentMatch(new RequestCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean responseObject, String message) {
                future.complete(ResponseEntity.ok().body(responseObject));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(ResponseEntity.badRequest().body(errorMessage));
            }
        });
        return ResponseEntityUtil.getObjectResponse(future);
    }

    @PostMapping("/set-next-match/{id}")
    public ResponseEntity<Object> setNextMatch(@PathVariable("id") String nextMatchId) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.liveScoreHandler().setNextMatch(nextMatchId, new RequestCallback<MatchDetailDto>() {
            @Override
            public void onSuccess(MatchDetailDto responseObject, String message) {
                future.complete(ResponseEntity.ok().body(responseObject));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(ResponseEntity.badRequest().body(errorMessage));
            }
        });
        return ResponseEntityUtil.getObjectResponse(future);
    }

    @PostMapping("/commit-final-score")
    public ResponseEntity<Object> submitFinalScore() {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.liveScoreHandler().commitFinalScore(new RequestCallback<Score[]>() {
            @Override
            public void onSuccess(Score[] responseObject, String message) {
                future.complete(ResponseEntity.ok().body(responseObject));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(ResponseEntity.badRequest().body(errorMessage));
            }
        });
        return ResponseEntityUtil.getObjectResponse(future);
    }

    @PostMapping("/override-score/{allianceId}")
    public ResponseEntity<Object> overrideScore(@PathVariable("allianceId") String allianceId, @RequestBody String jsonScoreData) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.liveScoreHandler().overrideScore(allianceId, jsonScoreData, new RequestCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean responseObject, String message) {
                future.complete(ResponseEntity.ok().body(responseObject));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(ResponseEntity.badRequest().body(errorMessage));
            }
        });
        return ResponseEntityUtil.getObjectResponse(future);
    }

    @PostMapping("/abort-current-match")
    public ResponseEntity<Object> abortCurrentMatch() {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.liveScoreHandler().abortCurrentMatch(new RequestCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean responseObject, String message) {
                future.complete(ResponseEntity.ok().body(responseObject));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(ResponseEntity.badRequest().body(errorMessage));
            }
        });
        return ResponseEntityUtil.getObjectResponse(future);
    }

}
