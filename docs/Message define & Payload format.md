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
2. **Score Updates**
   - Live Score Updates (during active matches)
     - Topic: `/topic/live/field/{fieldId}/score/{red|blue}`
     - Payload:
       ```json
       {
         "type": "SCORE_UPDATE",
         "payload": {
           "allianceId": "Q1_B",
           "totalScore": 145,
           "whiteBallsScored": 8,
           "goldenBallsScored": 3,
           "barriersPushed": 2,
           "partialParking": 1,
           "fullParking": 1,
           "penaltyCount": 0,
           "imbalanceCategory": 2,
           "timestamp": "2025-11-29T22:15:30Z"
         }
       }
       ```
   - Score Snapshot (during referee input)
     - Frontend Topic: `/app/live/score/update/{red|blue}`
     - Backend Broadcast: `/topic/live/field/{fieldId}/score/{red|blue}`
     - Payload:
       ```json
       {
         "type": "LIVE_SCORE_SNAPSHOT",
         "payload": {
           "matchId": "Q1",
           "alliance": "red",
           "version": 42,
           "sourceId": "ref-f3g2h1j8",
           "at": "2025-11-29T22:15:45.123Z",
           "state": {
             "whiteBallsScored": 7,
             "goldenBallsScored": 0,
             "barriersPushed": 3,
             "partialParking": 2,
             "fullParking": 0,
             "penaltyCount": 1,
             "imbalanceCategory": 1
           },
           "lastChange": { "key": "partialParking", "reason": "inc", "value": 2 }
         }
       }
       ```
   - Committed Final Scores
     - Topic: `/topic/display/field/{fieldId}/score/{red|blue}`
     - Payload:
       ```json
       {
         "type": "SCORE_UPDATE",
         "payload": {
           "allianceId": "Q1_R",
           "committed": true,
           "totalScore": 185,
           "biologicalPoints": 80,
           "barrierPoints": 60,
           "endGamePoints": 35,
           "penalties": 10,
           "balancingCoeff": 1.25,
           "breakdown": {
             "whiteBalls": { "count": 15, "points": 15 },
             "goldenBalls": { "count": 5, "points": 15 },
             "barriersPushed": { "count": 3, "points": 30 },
             "partialParking": { "count": 1, "points": 5 },
             "fullParking": { "count": 2, "points": 20, "fleetBonus": 10 }
           }
         }
       }
       ```

3. **Match Status Updates**
   - Match Lifecycle Events
     - Topic: `/topic/match/status`
     - Payload:
       ```json
       {
         "type": "MATCH_STATUS",
         "payload": {
           "matchId": "Q1",
           "status": "ACTIVE|COMPLETED|ABORTED",
           "fieldNumber": 1,
           "redAlliance": "Teams 5, 15, 31",
           "blueAlliance": "Teams 3, 22, 42",
           "startTime": "2025-11-29T22:00:00Z",
           "endTime": "2025-11-29T22:02:30Z"
         }
       }
       ```
   - Available Matches
     - Topic: `/topic/match/available`
     - Payload:
       ```json
       {
         "type": "MATCH_AVAILABLE",
         "payload": {
           "currentMatch": { "id": "Q1", "matchCode": "Q1", "type": "qualification" },
           "nextMatch": { "id": "Q2", "matchCode": "Q2", "type": "qualification" }
         }
       }
       ```

4. **System Administration**
   - Error Messages
     - Topic: `/topic/error/{userType}`
     - Payload:
       ```json
       {
         "type": "ERROR_MESSAGE",
         "payload": {
           "code": "SCORE_VALIDATION_FAILED",
           "message": "Alliance total exceeds maximum allowed score",
           "details": { "alliance": "red", "submitted": 450, "maximum": 400 },
           "timestamp": "2025-11-29T22:16:00Z"
         }
       }
       ```

## Message Flow Patterns

### Live Scoring During Match
```
Referee Input → Frontend Broadcast → Backend Processing → Live Display Update
     ↓              ↓                      ↓                      ↓
Interactive UI  →  /app/live/score/update →  LiveScoreHandler   → /topic/live/field/*/score
```

### Final Score Commitment
```
Referee Submit → Operator Commit → Backend Finalization → Public Display
     ↓               ↓                     ↓                      ↓
Temp Storage   →  /api/commit-scores   →  Score Persistence   →  /topic/display/field/*/score
```

## WebSocket Connection Details

**Endpoint:** `/ws`
**Protocol:** STOMP over SockJS
**Heartbeat:** 10000ms client, 10000ms server
**Reconnection:** Automatic with exponential backoff
**Authentication:** JWT token passed in STOMP CONNECT headers

