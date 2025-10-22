package org.thingai.app.scoringservice.handler.systembase;

import org.thingai.app.scoringservice.define.BroadcastMessageType;
import org.thingai.app.scoringservice.dto.MatchTimeStatusDto;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MatchTimerHandler {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> timerTask;

    private int remainingSeconds;
    private String matchId;
    private boolean isRunning;
    private BroadcastHandler broadcastHandler;

    private TimerCallback callback;

    public void startTimer(String matchId, int totalSeconds) {
        stopTimer();
        this.matchId = matchId;
        this.remainingSeconds = totalSeconds;
        this.timerTask = scheduler.scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.SECONDS);
    }

    public void pauseTimer() {
        isRunning = false;
        if (timerTask != null) timerTask.cancel(false);
    }

    public void resumeTimer() {
        if (!isRunning) {
            isRunning = true;
            timerTask = scheduler.scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.SECONDS);
        }
    }

    public void stopTimer() {
        isRunning = false;
        if (timerTask != null) timerTask.cancel(true);
        broadcastUpdate();
    }

    private void tick() {
        if (remainingSeconds > 0) {
            remainingSeconds--;
            broadcastUpdate();
        } else {
            stopTimer();
            callback.onTimerEnded(matchId);
        }
    }

    private void broadcastUpdate() {
        MatchTimeStatusDto dto = new MatchTimeStatusDto(matchId, remainingSeconds);
        String topic = "/topic/display/timer/" + matchId;
        broadcastHandler.broadcast(topic, dto, BroadcastMessageType.MATCH_STATUS);
    }

    public void setCallback(TimerCallback callback) {
        this.callback = callback;
    }

    public void setBroadcastHandler(BroadcastHandler broadcastHandler) {
        this.broadcastHandler = broadcastHandler;
    }

    public interface TimerCallback {
        void onTimerEnded(String matchId);
    }
}

