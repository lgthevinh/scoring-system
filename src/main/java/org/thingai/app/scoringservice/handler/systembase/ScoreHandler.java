package org.thingai.app.scoringservice.handler.systembase;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.BroadcastMessageType;
import org.thingai.app.scoringservice.define.ErrorCode;
import org.thingai.app.scoringservice.define.ScoreStatus;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.entity.score.ScoreSeasonDemo;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFile;

public class ScoreHandler {
    private final Dao dao;
    private final DaoFile daoFile;
    private final ObjectMapper objectMapper = new ObjectMapper(); // For converting DTO to JSON

    private BroadcastHandler broadcastHandler;

    public ScoreHandler(Dao dao, DaoFile daoFile) {
        this.dao = dao;
        this.daoFile = daoFile;
    }

    /**
     * Factory method to create a Score object. For a new season, change the implementation here.
     * @return A new Score object instance.
     */
    public static Score factoryScore() {
        return new ScoreSeasonDemo();
    }

    /**
     * Retrieves a score object for a specific alliance, fully populated with its raw details.
     * @param allianceId The unique ID of the alliance (e.g., "Q1_R").
     * @param callback   Callback to return the populated Score object or an error.
     */
    public void getScoreByAllianceId(String allianceId, RequestCallback<String> callback) {
        try {
            // 1. Read the base score object from the database.
            Score score = dao.read(Score.class, allianceId);
            if (score == null) {
                callback.onFailure(ErrorCode.NOT_FOUND, "Score not found for alliance: " + allianceId);
                return;
            }

            // 2. Read the detailed raw score data from the corresponding JSON file.
            String jsonRawScoreData = daoFile.readJsonFile("/scores/" + allianceId + ".json");
            if (jsonRawScoreData == null || jsonRawScoreData.isEmpty()) {
                // Generate json data
                Score newScore = factoryScore();
                newScore.setAllianceId(allianceId);
                daoFile.writeJsonFile("/scores/" + allianceId + ".json", newScore.getRawScoreData());
                jsonRawScoreData = newScore.getRawScoreData();
            }

            callback.onSuccess(jsonRawScoreData, "Score retrieved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to retrieve score: " + e.getMessage());
        }
    }

    public void getScoresByMatchId(String matchId, RequestCallback<String> callback) {
        try {
            // Assuming alliance IDs are formatted as "Q{matchId}_R" and "Q{matchId}_B"
            String redAllianceId = matchId + "_R";
            String blueAllianceId = matchId + "_B";

            Score redScore = dao.read(Score.class, redAllianceId);
            Score blueScore = dao.read(Score.class, blueAllianceId);

            if (redScore == null && blueScore == null) {
                callback.onFailure(ErrorCode.NOT_FOUND, "No scores found for match: " + matchId);
                return;
            }

            String redJsonData = daoFile.readJsonFile("/scores/" + redAllianceId + ".json");
            String blueJsonData = daoFile.readJsonFile("/scores/" + blueAllianceId + ".json");

            String result = "{";
            if (redScore != null) {
                result += "\"red\":" + (redJsonData != null ? redJsonData : "{}");
            } else {
                result += "\"red\":null";
            }

            result += ",";
            if (blueScore != null) {
                result += "\"blue\":" + (blueJsonData != null ? blueJsonData : "{}");
            } else {
                result += "\"blue\":null";
            }

            callback.onSuccess(result, "Scores retrieved successfully for match: " + matchId);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to retrieve scores for match: " + e.getMessage());
        }
    }

    /**
     * Takes raw scoring data (as a DTO), calculates the final score, and persists the result.
     * @param allianceId      The unique ID of the alliance being scored.
     * @param scoreDetailsDto A DTO representing the raw scoring inputs from the UI.
     * @param callback        Callback to signal completion.
     */
    public void submitScore(String allianceId, Object scoreDetailsDto, boolean isForceUpdate, RequestCallback<Score> callback) {
        try {
            // 1. Retrieve the existing score object.
            Score score = dao.read(Score.class, allianceId);
            if (score == null) {
                callback.onFailure(ErrorCode.NOT_FOUND, "Cannot submit score, match/alliance not found: " + allianceId);
                return;
            }

            if (score.getStatus() == ScoreStatus.SCORED && !isForceUpdate) {
                callback.onFailure(ErrorCode.UPDATE_FAILED, "Score already submitted for alliance: " + allianceId);
                return;
            }

            // 2. Convert the incoming DTO to a JSON string.
            Score finalScore = factoryScore();
            finalScore.setAllianceId(allianceId);
            String rawJsonData = objectMapper.writeValueAsString(scoreDetailsDto);

            // 3. Use the entity's fromJson method to populate its internal state.
            finalScore.fromJson(rawJsonData);

            // 4. Trigger the calculation logic within the finalScore object.
            finalScore.calculateTotalScore();
            finalScore.calculatePenalties();
            finalScore.setStatus(ScoreStatus.SCORED);

            // 5. Call the existing save method to persist the changes.
            updateAndSaveScore(finalScore, new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void result, String message) {
                    broadcastHandler.broadcast("/topic/scores", finalScore, BroadcastMessageType.SCORE_UPDATE);
                    callback.onSuccess(finalScore, "Score submitted and calculated successfully.");
                }

                @Override
                public void onFailure(int errorCode, String errorMessage) {
                    callback.onFailure(errorCode, errorMessage);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(ErrorCode.UPDATE_FAILED, "Failed to submit score: " + e.getMessage());
        }
    }

    /**
     * Updates the score in the database and writes its raw data to a JSON file.
     * @param score The fully calculated Score object.
     * @param callback Callback to signal completion.
     */
    public void updateAndSaveScore(Score score, RequestCallback<Void> callback) {
        try {
            // Get the raw data from the entity itself.
            String jsonRawScoreData = score.getRawScoreData();

            // Use the concrete class for the DAO operation.
            dao.update(Score.class, score.getAllianceId(), score);

            // Also write the raw data to a backup/detail JSON file.
            daoFile.writeJsonFile("/scores/" + score.getAllianceId() + ".json", jsonRawScoreData);

            callback.onSuccess(null, "Score data saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(ErrorCode.UPDATE_FAILED,"Failed to save score data: " + e.getMessage());
        }
    }

    public void setBroadcastHandler(BroadcastHandler broadcastHandler) {
        this.broadcastHandler = broadcastHandler;
    }
}

