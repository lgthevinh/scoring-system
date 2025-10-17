# Defined Message Types and Payloads

This document outlines the defined message types and their corresponding payload formats used in our communication protocol. This project use Spring WebSocket with STOMP protocol for real-time messaging.

To ensure consistent communication, all messages sent over the WebSocket will follow a standard structure.

## Message Structure

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
1. **Score Update**
- Topic: `/topic/scores`
- Message Type: `SCORE_UPDATE`
- Description: Sent whenever a score for an alliance is submitted or changed. 
Example Payload:
```json
{
    "type": "SCORE_UPDATE",
    "payload": {
        "allianceId": 1001,
        "score": {
            "id": "Q1_R",
            "status": 1,
            "totalScore": 125,
            "penaltiesScore": 15,
            "rawScoreData": {
                "robotHanged":1,
                "robotParked":2,
                "ballEntered":20,
                "minorFault":1,"majorFault":1
            }
        }
    }
}
```

2. **Current Match Change** 
- Topic: `/topic/match-state`
- Type: `CURRENT_MATCH_UPDATE`
- Description: Sent by an admin or field controller to designate which match is currently active on the main field.
Example Payload:
```json
{
    "type": "CURRENT_MATCH_UPDATE",
    "payload": {
        "match": {
            "id": "Q2",
            "matchCode": "Q2",
            "matchType": 1,
            "matchNumber": 2,
            "matchStartTime": "2025-10-18T10:15:00"
        },
        "redTeams": [
            { "teamId": "103", "teamName": "RoboRaiders", "teamSchool": "Tech High" },
            { "teamId": "104", "teamName": "Circuit Breakers", "teamSchool": "STEM Academy" }
        ],
        "blueTeams": [
            { "teamId": "203", "teamName": "Voltage", "teamSchool": "Innovation High" },
            { "teamId": "204", "teamName": "MechaKnights", "teamSchool": "Engineering Prep" }
        ]
    }
}
```
3. **Match Timer State**
- Topic: `/topic/match-timer`
- Type: `MATCH_TIMER_UPDATE`
- Description:  Sent periodically during a match to update the match clock.
Example Payload:
```json
{
    "type": "MATCH_TIMER_UPDATE",
    "payload": {
        "matchId": "Q2",
        "timeRemaining": 120, // in seconds
        "state": "IN_PROGRESS" // Possible values: NOT_STARTED, IN_PROGRESS, PAUSED, FINISHED
    }
}
```