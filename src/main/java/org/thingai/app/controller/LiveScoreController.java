package org.thingai.app.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.thingai.app.scoringservice.ScoringService;
import org.thingai.base.log.ILog;

@Controller
public class LiveScoreController {

    @MessageMapping("/live/score/update/blue")
    public void handleLiveScoreUpdate(@Payload String updateDto) {
        ILog.d("Received live score update of BLUE alliance", updateDto);

        ScoringService.liveScoreHandler().handleLiveScoreUpdate(updateDto, false);
    }

    @MessageMapping("/live/score/update/red")
    public void handleLiveScoreUpdateRed(@Payload String updateDto) {
        ILog.d("Received live score update of RED alliance ", updateDto);

        ScoringService.liveScoreHandler().handleLiveScoreUpdate(updateDto, true);
    }

}
