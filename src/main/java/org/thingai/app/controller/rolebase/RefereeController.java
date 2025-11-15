package org.thingai.app.controller.rolebase;

import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thingai.app.scoringservice.ScoringService;
import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.handler.LiveScoreHandler;

import java.util.Map;

@Controller
@RequestMapping("/api/ref")
public class RefereeController {

    @GetMapping("/{field}/available-matches")
    public RequestEntity<Object> getAvailableMatches(
            @RequestParam(name = "color", required = true) boolean isRed,
            @PathVariable String field
    ) {
        if (isRed) {

        } else {

        }
        return null;
    }

    @PostMapping("/submit/{allianceId}/team-present")
    public RequestEntity<Object> submitTeamPresent(@RequestBody Map<String, Object> request) {

        return null;
    }

    @PostMapping("/submit/{color}/{allianceId}/final-score")
    public RequestEntity<Object> submitMatchScore(@PathVariable String color ,@PathVariable String allianceId, @RequestBody String jsonScoreData) {
        boolean isRed = color.equalsIgnoreCase("red");
        ScoringService.liveScoreHandler().handleScoreSubmission(isRed, allianceId, jsonScoreData, new RequestCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean responseObject, String message) {

            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {

            }
        });
        return null;
    }


}
