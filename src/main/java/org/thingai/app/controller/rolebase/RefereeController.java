package org.thingai.app.controller.rolebase;

import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/referee")
public class RefereeController {

    @GetMapping("/match/available")
    public RequestEntity<Object> getAvailableMatches(@RequestParam(name = "color", required = true) boolean isRed) {
        if (isRed) {

        } else {

        }
        return null;
    }

    @PostMapping("/match/submit/team-present")
    public RequestEntity<Object> submitTeamPresent(@RequestBody Map<String, Object> request) {

        return null;
    }

    @PostMapping("/match/submit/final-score")
    public RequestEntity<Object> submitMatchScore(@RequestBody Map<String, Object> request) {

        return null;
    }


}
