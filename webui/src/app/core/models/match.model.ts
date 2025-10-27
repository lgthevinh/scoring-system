import { Team } from "./team.model";

export interface Match {
  id: string;
  matchCode: string;
  matchType: number;
  matchNumber: number;
  matchField: number;
  matchStartTime: string;
  matchEndTime: string | null;
}

export interface MatchDetailDto {
  match: Match;
  redTeams: Team[];
  blueTeams: Team[];
}

export function SampleMatchDetailDto(size: number): MatchDetailDto[] {
  let sampleMatchDetails: MatchDetailDto[] = [];
  for (let i = 1; i <= size; i++) {
    sampleMatchDetails.push({
      match: {
        id: `match-${i}`,
        matchCode: `Q${i}`,
        matchType: 1,
        matchNumber: i,
        matchField: 1,
        matchStartTime: new Date().toISOString(),
        matchEndTime: null
      },
      redTeams: [
        { teamId: `R${i}1`, teamName: `Red Team ${i}1`, teamSchool: `School ${i}1`, teamRegion: `Region ${i}1` },
        { teamId: `R${i}2`, teamName: `Red Team ${i}2`, teamSchool: `School ${i}2`, teamRegion: `Region ${i}2` },
      ],
      blueTeams: [
        { teamId: `B${i}1`, teamName: `Blue Team ${i}1`, teamSchool: `School ${i}1`, teamRegion: `Region ${i}1` },
        { teamId: `B${i}2`, teamName: `Blue Team ${i}2`, teamSchool: `School ${i}2`, teamRegion: `Region ${i}2` },
      ]
    });
  }
  return sampleMatchDetails;

}
