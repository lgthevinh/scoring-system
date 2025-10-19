package org.thingai.app.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.thingai.app.scoringservice.dto.BroadcastMessageDto;
import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.base.log.ILog;

import java.util.Map;

@Controller
public class BroadcastController {
    private static final String TAG = "BroadcastController";

    private SimpMessagingTemplate messagingTemplate;

    public BroadcastController() {

    }

    public BroadcastController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/live-score-update")
    @SendTo("/topic/scores")
    public void liveScoreUpdateFromClient(String message) {
        ILog.d(TAG, "Received live score update from client: " + message);
    }

    public void broadcastScoreUpdate(Score score) {
        ILog.d(TAG, "Broadcasting score update: " + score);
        BroadcastMessageDto message = new BroadcastMessageDto("SCORE_UPDATE", score);
        messagingTemplate.convertAndSend("/topic/scores", message);
    }

    public void broadcastCurrentMatch(MatchDetailDto matchDetail) {
        ILog.d(TAG, "Broadcasting current match update: " + matchDetail);
        BroadcastMessageDto message = new BroadcastMessageDto("CURRENT_MATCH_UPDATE", matchDetail);
        messagingTemplate.convertAndSend("/topic/match-state", message);
    }

    public void broadcastTimerUpdate(String matchId, String state, int timeRemaining) {
        Map<String, Object> payload = Map.of(
                "matchId", matchId,
                "state", state,
                "timeRemaining", timeRemaining
        );
        BroadcastMessageDto message = new BroadcastMessageDto("TIMER_UPDATE", payload);
        messagingTemplate.convertAndSend("/topic/match-timer", message);
    }
}

