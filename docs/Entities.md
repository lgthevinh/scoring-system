# Database and Class model entities

## Relation Schema
![Relation Schema.png](Relation%20schema.png)

This schema shows the relations between the different entities in the database. The arrows indicate the direction of the relationship, with the arrow pointing to the entity that is being referenced.

### Entities explain
- `Match`
  - Represents a match in the game.
  - Contains information about the match, such as `id`, `matchStartTime`, `matchType`, `matchStatus`, `allianceRed` and `allianceBlue`.
- `Alliance`
  - Represents an alliance in the game.
  - Contains information about the alliance, such as `id`, `matchID`, `color`.
  - The `id` is built based on `match.type` and `match.number` (e.g. `1001` for `match.type = 1`, `match.number = 001`).
- `Score`
  - Represents the score of a match mapping with `Alliance`.
  - Contains information about the score, such as `id`, `allianceId`, and other attributes suitable with season.
- `Team`
  - Represents a team in the game.
  - Contains information about the team, such as `id`, `teamName`, `teamRegion`, `teamSchool` and other attributes suitable with season.
- `AllianceTeam`
  - This entity is used for relationship between `Alliance` and `Team`, serving one-to-many relationship (usually one alliance has 2 teams).
  - Contains information about the alliance team, such as `id`, `allianceId`, `teamId`.
- `TeamStats`
  - This entity is used to show statistic of a team in each `Match` and `Score`.
  - Contains information about the team stats, such as `id`, `matchId`, `teamId`, `allianceId`, and other attributes suitable with season.

### ID System
- Match ID: 2 bytes
  - 1 byte for match type (0: qualification, 1: elimination, 2: final)
  - 1 byte for match number (from `1` to `255`) of a match type
- Alliance ID: 3 bytes
  - 2 bytes for match ID (as described above)
  - 1 byte for alliance color (0: red, 1: blue)
- Score ID: 4 bytes
  - 3 bytes for alliance ID (as described above)
  - 1 byte for alliance color (0: red, 1: blue)
- Team ID: 2 bytes
  - 2 bytes for team number (from `00001` to `65535`)

In this system should have byte-to-integer conversion to handle ID generation and parsing. The conversion is done by shifting the bits of each byte to the left and combining them into a single integer.

This system allows for a unique ID for each entity, while also allowing each entity to be generated without dependencies on other entities. The IDs are designed to be compact and efficient, while still allowing for easy parsing and generation.
