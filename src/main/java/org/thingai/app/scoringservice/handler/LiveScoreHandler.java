package org.thingai.app.scoringservice.handler;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.BroadcastMessageType;
import org.thingai.app.scoringservice.define.ErrorCode;
import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.handler.entityhandler.MatchHandler;
import org.thingai.app.scoringservice.handler.entityhandler.ScoreHandler;
import org.thingai.base.log.ILog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LiveScoreHandler {
    private static final String TAG = "ScorekeeperHandler";
    private static final MatchTimerHandler matchTimerHandler = new MatchTimerHandler();

    private final MatchHandler matchHandler;
    private BroadcastHandler broadcastHandler;
    private ScoreHandler scoreHandler;

    private static MatchDetailDto currentMatch;
    private static MatchDetailDto nextMatch;

    private static Score currentRedScoreHolder;
    private static Score currentBlueScoreHolder;

    public LiveScoreHandler(MatchHandler matchHandler, ScoreHandler scoreHandler) {
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
            broadcastHandler.broadcast("/topic/match/available", nextMatch, BroadcastMessageType.MATCH_STATUS);
        } catch (Exception e) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to set next match: " + e.getMessage());
        }
    }

    public void startCurrentMatch(RequestCallback<Boolean> callback) {
        if (nextMatch == null) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "No match found");
        }

        currentMatch = nextMatch;
        nextMatch = null;

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime currentTime = LocalDateTime.now();

        currentMatch.getMatch().setActualStartTime(currentTime.format(timeFormatter));

        currentRedScoreHolder = ScoreHandler.factoryScore();
        currentBlueScoreHolder = ScoreHandler.factoryScore();

        currentBlueScoreHolder.setAllianceId(currentMatch.getMatch().getId() + "_B");
        currentRedScoreHolder.setAllianceId(currentMatch.getMatch().getId() + "_R");

        matchTimerHandler.startTimer(currentMatch.getMatch().getId(), currentMatch.getMatch().getFieldNumber(), 150);
        callback.onSuccess(true, "Match started");
    }

    public void handleLiveScoreUpdate(String updatedScoreJson, boolean isRedAlliance) {
        ILog.d(TAG, "Live Score Update Received: " + updatedScoreJson);

        if (isRedAlliance) {
            currentRedScoreHolder.fromJson(updatedScoreJson);
            broadcastHandler.broadcast("/topic/live/alliance/score/" + currentRedScoreHolder.getAllianceId(),
                    currentRedScoreHolder,
                    BroadcastMessageType.SCORE_UPDATE);
        } else {
            currentBlueScoreHolder.fromJson(updatedScoreJson);
            broadcastHandler.broadcast("/topic/live/alliance/score/" + currentBlueScoreHolder.getAllianceId(),
                    currentBlueScoreHolder,
                    BroadcastMessageType.SCORE_UPDATE);
        }
    }

    public void commitFinalScore(RequestCallback<Score[]> callback) {
        final Score[] result = new Score[2];
        scoreHandler.submitScore(currentBlueScoreHolder, true, new RequestCallback<Score>() {;
            @Override
            public void onSuccess(Score responseObject, String message) {
                ILog.d(TAG, "Red alliance score submitted: " + message);
                result[0] = responseObject;
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                ILog.d(TAG, "Failed to submit red alliance score: " + errorMessage);
            }
        });

        scoreHandler.submitScore(currentBlueScoreHolder, true, new RequestCallback<Score>() {
            @Override
            public void onSuccess(Score responseObject, String message) {
                ILog.d(TAG, "Blue alliance score submitted: " + message);
                result[1] = responseObject;
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                ILog.d(TAG, "Failed to submit blue alliance score: " + errorMessage);
            }
        });

        callback.onSuccess(result, "Scores committed successfully");

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

    public void abortCurrentMatch(RequestCallback<Boolean> callback) {
        matchTimerHandler.stopTimer();
        currentRedScoreHolder = null;
        currentBlueScoreHolder = null;
        callback.onSuccess(true, "Match aborted");
    }

    public void initialSyncFrontend(RequestCallback<MatchDetailDto[]> callback) {
        try {
            callback.onSuccess(new MatchDetailDto[]{currentMatch, nextMatch}, "Initial sync success");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Initial sync failed: " + e.getMessage());
        }
    }

    public void setBroadcastHandler(BroadcastHandler broadcastHandler) {
        ILog.d(TAG, "Broadcast Handler: " + broadcastHandler);
        this.broadcastHandler = broadcastHandler;
        this.matchTimerHandler.setBroadcastHandler(broadcastHandler);
    }
}
