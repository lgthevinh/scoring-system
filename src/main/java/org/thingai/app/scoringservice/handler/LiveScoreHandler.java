package org.thingai.app.scoringservice.handler;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.BroadcastMessageType;
import org.thingai.app.scoringservice.define.ErrorCode;
import org.thingai.app.scoringservice.dto.LiveScoreUpdateDto;
import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.app.scoringservice.dto.MatchTimeStatusDto;
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

    private static boolean isRedCommitable = false;
    private static boolean isBlueCommitable = false;

    public LiveScoreHandler(MatchHandler matchHandler, ScoreHandler scoreHandler) {
        this.matchHandler = matchHandler;
        this.scoreHandler = scoreHandler;

        matchTimerHandler.setCallback(new MatchTimerHandler.TimerCallback() {
            @Override
            public void onTimerEnded(String matchId) {
                ILog.d(TAG, "Match Timer Ended: " + matchId);
            }
            @Override
            public void onTimerUpdated(String matchId, String fieldNumber, int remainingSeconds) {
                MatchTimeStatusDto dto = new MatchTimeStatusDto(matchId, remainingSeconds);
                String topic = "/topic/display/field/" + fieldNumber + "/timer";
                broadcastHandler.broadcast(topic, dto, BroadcastMessageType.MATCH_STATUS);
            }
        });
    }

    public void setNextMatch(String matchId, RequestCallback<MatchDetailDto> callback) {
        try {
            nextMatch = matchHandler.getMatchDetailSync(matchId);
            callback.onSuccess(nextMatch, "Set next match success");
            broadcastHandler.broadcast("/topic/match/available", nextMatch, BroadcastMessageType.MATCH_STATUS);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to set next match: " + e.getMessage());
        }
    }

    public void startCurrentMatch(RequestCallback<Boolean> callback) {
        if (nextMatch == null) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "No match found");
        }

        currentMatch = nextMatch;
        nextMatch = null;

        broadcastHandler.broadcast("/topic/display/field/" + currentMatch.getMatch().getFieldNumber() + "/command",
                currentMatch,
                BroadcastMessageType.SHOW_TIMER);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime currentTime = LocalDateTime.now();

        currentMatch.getMatch().setActualStartTime(currentTime.format(timeFormatter));

        currentRedScoreHolder = ScoreHandler.factoryScore();
        currentBlueScoreHolder = ScoreHandler.factoryScore();

        currentBlueScoreHolder.setAllianceId(currentMatch.getMatch().getId() + "_B");
        currentRedScoreHolder.setAllianceId(currentMatch.getMatch().getId() + "_R");

        broadcastHandler.broadcast("/topic/live/field/" + currentMatch.getMatch().getFieldNumber() + "/score/red",
                currentRedScoreHolder,
                BroadcastMessageType.SCORE_UPDATE);

        broadcastHandler.broadcast("/topic/live/field/" + currentMatch.getMatch().getFieldNumber() + "/score/blue",
                currentBlueScoreHolder,
                BroadcastMessageType.SCORE_UPDATE);

        matchTimerHandler.startTimer(currentMatch.getMatch().getId(), currentMatch.getMatch().getFieldNumber(), 150);
        callback.onSuccess(true, "Match started");
    }

    public void handleLiveScoreUpdate(LiveScoreUpdateDto liveScoreUpdate, boolean isRedAlliance) {
        ILog.d(TAG, "Live Score Update Received: " + liveScoreUpdate);
        if (liveScoreUpdate.payload.matchId == null || !liveScoreUpdate.payload.matchId.equals(currentMatch.getMatch().getId())) {
            ILog.d(TAG, "Live score update match ID does not match current match ID");
            return;
        }

        if (isRedAlliance) {
            currentRedScoreHolder.fromJson(liveScoreUpdate.payload.state.toString());
            currentRedScoreHolder.calculateTotalScore();
            broadcastHandler.broadcast("/topic/live/field/" + currentMatch.getMatch().getFieldNumber() + "/score/red",
                    currentRedScoreHolder,
                    BroadcastMessageType.SCORE_UPDATE);
        } else {
            currentBlueScoreHolder.fromJson(liveScoreUpdate.payload.state.toString());
            currentBlueScoreHolder.calculateTotalScore();
            broadcastHandler.broadcast("/topic/live/field/" + currentMatch.getMatch().getFieldNumber() + "/score/blue",
                    currentBlueScoreHolder,
                    BroadcastMessageType.SCORE_UPDATE);
        }
    }

    public void commitFinalScore(RequestCallback<Score[]> callback) {
        if (!isRedCommitable || !isBlueCommitable) {
            callback.onFailure(ErrorCode.CUSTOM_ERR, "Scores are not commitable yet");
            return;
        }

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

        isRedCommitable = false;
        isBlueCommitable = false;
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

    public void getCurrentPlayingMatches(RequestCallback<MatchDetailDto[]> callback) {
        try {
            callback.onSuccess(new MatchDetailDto[]{currentMatch, nextMatch}, "Initial sync success");
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Initial sync failed: " + e.getMessage());
        }
    }

    public void getCurrentMatchField(int fieldNumber, RequestCallback<MatchDetailDto> callback) {
        try {
            if (fieldNumber == 0) {
                callback.onSuccess(currentMatch, "Current match field retrieved successfully");
                return;
            }
            if (currentMatch != null && currentMatch.getMatch().getFieldNumber() == fieldNumber) {
                callback.onSuccess(currentMatch, "Current match field retrieved successfully");
            } else if (nextMatch != null && nextMatch.getMatch().getFieldNumber() == fieldNumber) {
                callback.onSuccess(nextMatch, "Next match field retrieved successfully");
            } else {
                callback.onFailure(ErrorCode.NOT_FOUND, "No match found for field number: " + fieldNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to get current match field: " + e.getMessage());
        }
    }

    public void setBroadcastHandler(BroadcastHandler broadcastHandler) {
        ILog.d(TAG, "Broadcast Handler: " + broadcastHandler);
        this.broadcastHandler = broadcastHandler;
    }

    public void handleScoreSubmission(boolean isRed, String allianceId, String jsonScoreData, RequestCallback<Boolean> callback) {
        // Check if allianceId matches current match
        String currentAllianceId;
        boolean isCommited;
        if (isRed) {
            currentAllianceId = currentRedScoreHolder.getAllianceId();
            isCommited = isRedCommitable;
        } else {
            currentAllianceId = currentBlueScoreHolder.getAllianceId();
            isCommited = isBlueCommitable;
        }

        if (!allianceId.equals(currentAllianceId) && isCommited) {
            callback.onFailure(ErrorCode.CUSTOM_ERR, "Current alliance is not commitable");
            return;
        }

        try {
            Score submittedScore = ScoreHandler.factoryScore();
            submittedScore.setAllianceId(allianceId);
            submittedScore.fromJson(jsonScoreData);
            submittedScore.calculateTotalScore();
            submittedScore.calculatePenalties();

            // Update current score holder
            if (isRed) {
                currentRedScoreHolder = submittedScore;
                isRedCommitable = true;
            } else {
                currentBlueScoreHolder = submittedScore;
                isBlueCommitable = true;
            }

            ILog.d(TAG, "Score submission received for alliance " + allianceId + ": Total=" + submittedScore.getTotalScore() + ", Penalties=" + submittedScore.getPenaltiesScore());

            callback.onSuccess(true, "Score submission processed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(ErrorCode.CUSTOM_ERR, "Failed to process score submission: " + e.getMessage());
        }
    }
}
