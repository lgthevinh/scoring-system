package org.thingai.app.scoringservice.dto;

/**
 * A generic wrapper for all messages sent over WebSocket.
 * This provides a consistent structure for the frontend to handle.
 */
public class BroadcastMessageDto {
    private String type;
    private Object payload;

    public BroadcastMessageDto(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    // Standard Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}

