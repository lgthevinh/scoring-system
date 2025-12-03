package org.thingai.app.controller;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thingai.app.controller.utils.ResponseEntityUtil;
import org.thingai.app.scoringservice.ScoringService;
import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.entity.ranking.RankingEntry;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/rank")
public class RankController {
    @GetMapping("/status")
    public ResponseEntity<Object> getRankStatus() {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.rankingHandler().getRankingStatus(new RequestCallback<RankingEntry[]>() {
            @Override
            public void onSuccess(RankingEntry[] responseObject, String message) {
                future.complete(ResponseEntity.ok().body(responseObject));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(ResponseEntity.status(errorCode).body(null));
            }
        });
        return ResponseEntityUtil.getObjectResponse(future);
    }

    @PostMapping("/recalculate")
    public ResponseEntity<Object> recalculateRaking(RequestEntity<Void> requestEntity) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.rankingHandler().recalculateRankings(new RequestCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean responseObject, String message) {
                future.complete(ResponseEntity.ok().body(responseObject));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(ResponseEntity.status(errorCode).body(null));
            }
        });
        return ResponseEntityUtil.getObjectResponse(future);
    }
}
