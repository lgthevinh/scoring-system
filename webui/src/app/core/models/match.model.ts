import { Team } from "./team.model";

export interface Match {
  id: string;
  matchCode: string;
  matchType: number;
  matchNumber: number;
  matchStartTime: string;
  matchEndTime: string | null;
}

export interface MatchDetailDto {
  match: Match;
  redTeams: Team[];
  blueTeams: Team[];
}
