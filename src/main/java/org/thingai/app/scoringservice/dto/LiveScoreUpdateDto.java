package org.thingai.app.scoringservice.dto;

public class LiveScoreUpdateDto {
    public String type;
    public LiveScorePayload payload;

    public static class LiveScorePayload {
        public String matchId;
        public String alliance;
        public int version;
        public String sourceId;
        public String at;
        public Object state;
        public LiveScoreChange lastChange;
    }

    public static class LiveScoreChange {
        public String key;
        public String reason;
        public int value;
    }
}