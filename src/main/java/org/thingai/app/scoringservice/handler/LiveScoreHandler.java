package org.thingai.app.scoringservice.handler;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.BroadcastMessageType;
import org.thingai.app.scoringservice.define.ErrorCode;
import org.thingai.app.scoringservice.define.ScoreStatus;
import org.thingai.app.scoringservice.dto.LiveScoreUpdateDto;
import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.app.scoringservice.dto.MatchTimeStatusDto;
import org.thingai.app.scoringservice.entity.match.Match;
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
    private final ScoreHandler scoreHandler;

    private BroadcastHandler broadcastHandler;

    private MatchDetailDto currentMatch;
    private MatchDetailDto nextMatch;
    private Score currentRedScoreHolder;
    private Score currentBlueScoreHolder;

    private int typicalMatchDuration = 150; // seconds

    /* Flags */
    private boolean isRedCommitable = false;
    private boolean isBlueCommitable = false;

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

    /**
     * Set the next match to be played, the broadcast notify all clients about the new match set.
     * @param matchId
     * @param callback
     */
    public void setNextMatch(String matchId, RequestCallback<MatchDetailDto> callback) {
        try {
            // Retrieve match detail and return callback response
            nextMatch = matchHandler.getMatchDetailSync(matchId);
            broadcastHandler.broadcast("/topic/match/available", nextMatch, BroadcastMessageType.MATCH_STATUS);

            callback.onSuccess(nextMatch, "Set next match success");
            // Broadcast the next match info to all clients
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to set next match: " + e.getMessage());
        }
    }

    public void startCurrentMatch(RequestCallback<Boolean> callback) {
        if (nextMatch == null) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "No match found");
        }

        try {
            currentMatch = nextMatch;
            nextMatch = null;

            int fieldNumber = currentMatch.getMatch().getFieldNumber();
            String rootTopic = "/topic/display/field/" + fieldNumber;

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime currentTime = LocalDateTime.now();

            currentMatch.getMatch().setActualStartTime(currentTime.format(timeFormatter));

            currentRedScoreHolder = ScoreHandler.factoryScore();
            currentBlueScoreHolder = ScoreHandler.factoryScore();

            currentBlueScoreHolder.setAllianceId(currentMatch.getMatch().getId() + "_B");
            currentRedScoreHolder.setAllianceId(currentMatch.getMatch().getId() + "_R");

            matchTimerHandler.startTimer(currentMatch.getMatch().getId(), fieldNumber, typicalMatchDuration); // 2:30 -> 150 seconds

            broadcastHandler.broadcast(rootTopic +  "/command", currentMatch, BroadcastMessageType.SHOW_TIMER);
            broadcastHandler.broadcast(rootTopic +  "/score/red", currentRedScoreHolder, BroadcastMessageType.SCORE_UPDATE);
            broadcastHandler.broadcast(rootTopic +  "/score/blue", currentBlueScoreHolder, BroadcastMessageType.SCORE_UPDATE);

            callback.onSuccess(true, "Match started");
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(ErrorCode.CUSTOM_ERR, "Failed to start match: " + e.getMessage());
        }
    }

    public void handleLiveScoreUpdate(LiveScoreUpdateDto liveScoreUpdate, boolean isRedAlliance) {
        ILog.d(TAG, "Live Score Update Received: " + liveScoreUpdate);
        if (liveScoreUpdate.payload.matchId == null || !liveScoreUpdate.payload.matchId.equals(currentMatch.getMatch().getId())) {
            ILog.d(TAG, "Live score update match ID does not match current match ID");
            return;
        }

        try {
            int fieldNumber = currentMatch.getMatch().getFieldNumber();
            String rootTopic = "/topic/live/field/" + fieldNumber;

            if (isRedAlliance) {
                currentRedScoreHolder.fromJson(liveScoreUpdate.payload.state.toString());
                currentRedScoreHolder.calculateTotalScore();
                broadcastHandler.broadcast(rootTopic + "/score/red", currentRedScoreHolder, BroadcastMessageType.SCORE_UPDATE);
            } else {
                currentBlueScoreHolder.fromJson(liveScoreUpdate.payload.state.toString());
                currentBlueScoreHolder.calculateTotalScore();
                broadcastHandler.broadcast(rootTopic + "/score/blue", currentBlueScoreHolder, BroadcastMessageType.SCORE_UPDATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ILog.d(TAG, "Failed to process live score update: " + e.getMessage());
        }


    }

    public void commitFinalScore(RequestCallback<Score[]> callback) {
        if (!isRedCommitable || !isBlueCommitable) {
            callback.onFailure(ErrorCode.CUSTOM_ERR, "Scores are not commitable yet");
            return;
        }

        // Re-calculate final scores
        currentRedScoreHolder.calculatePenalties();
        currentRedScoreHolder.calculateTotalScore();
        currentBlueScoreHolder.calculatePenalties();
        currentBlueScoreHolder.calculateTotalScore();

        final Score[] result = new Score[2];
        scoreHandler.submitScore(currentRedScoreHolder, true, new RequestCallback<Score>() {;
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

        // update current match end time
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime currentTime = LocalDateTime.now();

        currentMatch.getMatch().setMatchEndTime(currentTime.format(timeFormatter));
        matchHandler.updateMatch(currentMatch.getMatch(), new RequestCallback<Match>() {
            @Override
            public void onSuccess(Match responseObject, String message) {
                ILog.d(TAG, "Match end time updated: " + message);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                ILog.d(TAG, "Failed to update match end time: " + errorMessage);
            }
        });

        isRedCommitable = false;
        isBlueCommitable = false;

        callback.onSuccess(result, "Scores committed successfully");
    }

    /**
     * Override the score for an alliance
     * @param allianceId
     * @param jsonScoreData format for this params should only contain the detail score element according to the season.
     *                    <p>Example {"robotHanged": 2, "robotParked": 1, "ballCollected": 15}</p>
     * @param callback
     */
    public void overrideScore(String allianceId, String jsonScoreData, RequestCallback<Boolean> callback) {
        ILog.d(TAG, "Override score request received for alliance " + allianceId + ": " + jsonScoreData);
        Score targetScore = ScoreHandler.factoryScore();
        try {
            targetScore.setAllianceId(allianceId);
            targetScore.fromJson(jsonScoreData);
            targetScore.calculatePenalties();
            targetScore.calculateTotalScore();
            targetScore.setStatus(ScoreStatus.SCORED);

            scoreHandler.submitScore(targetScore, true, new RequestCallback<Score>() {
                @Override
                public void onSuccess(Score responseObject, String message) {
                    ILog.d(TAG, "Score override submitted for alliance " + allianceId + ": " + message);
                    callback.onSuccess(true, "Score override successful");
                }

                @Override
                public void onFailure(int errorCode, String errorMessage) {
                    ILog.d(TAG, "Failed to submit score override for alliance " + allianceId + ": " + errorMessage);
                    callback.onFailure(errorCode, "Score override failed: " + errorMessage);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(ErrorCode.CUSTOM_ERR, "Failed to parse score data: " + e.getMessage());
        }
    }

    public void abortCurrentMatch(RequestCallback<Boolean> callback) {
        matchTimerHandler.stopTimer();
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
            Score submittedScore;

            // Update current score holder
            if (isRed) {
                submittedScore = currentRedScoreHolder;
                isRedCommitable = true;
            } else {
                submittedScore = currentBlueScoreHolder;
                isBlueCommitable = true;
            }

            submittedScore.fromJson(jsonScoreData);
            submittedScore.calculatePenalties();
            submittedScore.calculateTotalScore();
            submittedScore.setStatus(ScoreStatus.SCORED);

            ILog.d(TAG, "Score submission received for alliance " + allianceId + ": Total=" + submittedScore.getTotalScore() + ", Penalties=" + submittedScore.getPenaltiesScore());

            callback.onSuccess(true, "Score submission processed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(ErrorCode.CUSTOM_ERR, "Failed to process score submission: " + e.getMessage());
        }
    }

    public void setBroadcastHandler(BroadcastHandler broadcastHandler) {
        this.broadcastHandler = broadcastHandler;
    }
}
