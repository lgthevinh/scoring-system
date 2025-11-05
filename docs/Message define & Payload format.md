# Defined Message Types and Payloads

This document outlines the defined message types and their corresponding payload formats used in our communication protocol. This project use Spring WebSocket with STOMP protocol for real-time messaging.

To ensure consistent communication, all messages sent over the WebSocket will follow a standard structure.

## Table of Contents
1. [System Message Structure](#system-message-structure)
2. [Defined Message Types and Payload Formats](#defined-message-types-and-payload-formats)
3. 

## System Message Structure

### Generic Message Wrapper

Every message will be a JSON object with two top-level properties:
- `type`: A string enum that identifies the type of event (e.g., "SCORE_UPDATE").
- `payload`: A JSON object containing the data for that event.

```json
{
    "type": "EVENT_TYPE_STRING",
    "payload": { ... }
}
```
### Defined Message Types and Payload Formats
1. **Display**
- Timer 
  - Topic `/topic/display/field/{fieldNumber}/timer`
  - Payload:
    ```json
    {
      "type": "FIELD_TIMER",
      "payload": {
        "matchId": "Q1",
        "remainingSeconds": 150
      }
    }
    ```
- Command
  - Topic `/topic/display/field/{fieldNumber}/command`
  - Payload:
    ```json
    {
      "type": "SHOW_SPONSORS" | "SHOW_BLANK" | "SHOW_UPNEXT" | "SHOW_TIMER"
      "payload": {
        ...
      }
    }
    ```
2. **Score**