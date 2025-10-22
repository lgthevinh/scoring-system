package org.thingai.app.scoringservice.handler.rolebase;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.ErrorCode;
import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.handler.systembase.BroadcastHandler;
import org.thingai.app.scoringservice.handler.systembase.MatchHandler;
import org.thingai.app.scoringservice.handler.systembase.MatchTimerHandler;
import org.thingai.app.scoringservice.handler.systembase.ScoreHandler;
import org.thingai.base.log.ILog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScorekeeperHandler {
    private static final String TAG = "ScorekeeperHandler";
    private static final MatchTimerHandler matchTimerHandler = new MatchTimerHandler();

    private final MatchHandler matchHandler;
    private BroadcastHandler broadcastHandler;
    private ScoreHandler scoreHandler;

    private static MatchDetailDto currentMatch;
    private static MatchDetailDto nextMatch;

    private static Score currentRedScoreHolder;
    private static Score currentBlueScoreHolder;

    public ScorekeeperHandler(MatchHandler matchHandler, ScoreHandler scoreHandler) {
        this.matchHandler = matchHandler;
        this.scoreHandler = scoreHandler;

        matchTimerHandler.setCallback(new MatchTimerHandler.TimerCallback() {
            @Override
            public void onTimerEnded(String matchId) {
                ILog.d(TAG, "Match Timer Ended: " + matchId);
            }
        });
    }

    public void setNextMatch(String matchId, RequestCallback<MatchDetailDto> callback) {
        try {
            nextMatch = matchHandler.getMatchDetailSync(matchId);
            callback.onSuccess(nextMatch, "Set next match success");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to set next match: " + e.getMessage());
        }
    }

    public void startCurrentMatch(RequestCallback<Boolean> callback) {
        if (currentMatch == null && nextMatch == null) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "No match found");
        } else if (currentMatch == null) {
            currentMatch = nextMatch;
            nextMatch = null;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime currentTime = LocalDateTime.now();

        currentMatch.getMatch().setActualStartTime(currentTime.format(timeFormatter));

        matchTimerHandler.startTimer(currentMatch.getMatch().getId(), 150);
        callback.onSuccess(true, "Match started");
    }

    public void commitScore(String allianceId, RequestCallback<Boolean> callback) {
        // Submit current score holder


        // Update next match to current match
        currentMatch = nextMatch;
        nextMatch = null;
    }

    /**
     * Override the score for an alliance
     * @param allianceId
     * @param detailScore format for this params should only contain the detail score element according to the season.
     *                    <p>Example {"robotHanged": 2, "robotParked": 1, "ballCollected": 15}</p>
     * @param callback
     */
    public void overrideScore(String allianceId, Object detailScore, RequestCallback<Boolean> callback) {

    }

    public void setBroadcastHandler(BroadcastHandler broadcastHandler) {
        ILog.d(TAG, "Broadcast Handler: " + broadcastHandler);
        this.broadcastHandler = broadcastHandler;
        this.matchTimerHandler.setBroadcastHandler(broadcastHandler);
    }
}
