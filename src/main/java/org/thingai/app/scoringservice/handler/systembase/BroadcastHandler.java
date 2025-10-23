package org.thingai.app.scoringservice.handler.systembase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.thingai.app.scoringservice.dto.BroadcastMessageDto;

@Service
public class BroadcastHandler {
    private static final String TAG = "BroadcastHandler";

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public BroadcastHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Broadcast a message to a specific topic.
     *
     * @param topic   The topic to broadcast to (e.g., "/topic/scores").
     * @param message The message payload.
     */
    public void broadcast(String topic, Object message, String messageType) {
        BroadcastMessageDto broadcastMessage = new BroadcastMessageDto(messageType, message);
        messagingTemplate.convertAndSend(topic, broadcastMessage);
    }
}