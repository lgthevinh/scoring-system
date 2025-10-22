package org.thingai.app.controller.rolebase;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thingai.app.controller.utils.ResponseEntityUtil;
import org.thingai.app.scoringservice.ScoringService;
import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.base.log.ILog;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/scorekeeper")
public class ScorekeeperController {
    private static final String TAG = "ScorekeeperController";

    @PostMapping("/start-current-match")
    public ResponseEntity<Object> startMatch() {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.scorekeeperHandler().startCurrentMatch(new RequestCallback<Boolean>() {
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

    @PostMapping("/set-next-match")
    public ResponseEntity<Object> setNextMatch(@RequestParam String nextMatchId) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.scorekeeperHandler().setNextMatch(nextMatchId, new RequestCallback<MatchDetailDto>() {
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


}
