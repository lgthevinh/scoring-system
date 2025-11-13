package org.thingai.app.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.thingai.app.scoringservice.ScoringService;
import org.thingai.app.scoringservice.dto.LiveScoreUpdateDto;
import org.thingai.base.log.ILog;

@Controller
public class LiveScoreController {

    @MessageMapping("/live/score/update/blue")
    public void handleLiveScoreUpdate(@Payload LiveScoreUpdateDto updateDto) {
        ILog.d("Received live score update of BLUE alliance", updateDto.payload.at);

        ScoringService.liveScoreHandler().handleLiveScoreUpdate(updateDto, false);
    }

    @MessageMapping("/live/score/update/red")
    public void handleLiveScoreUpdateRed(@Payload LiveScoreUpdateDto updateDto) {
        ILog.d("Received live score update of RED alliance ", updateDto.payload.at);

        ScoringService.liveScoreHandler().handleLiveScoreUpdate(updateDto, true);
    }

}
