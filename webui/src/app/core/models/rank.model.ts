/**
 * Represents a team's ranking entry in FRC qualification rankings.
 *
 * Contains all statistics accumulated during qualification matches,
 * used to determine playoff seeding.
 */
export interface RankingEntry {
  /** Unique team identifier (e.g., "1234") */
  teamId: string;

  /** Number of qualification matches this team has played */
  matchesPlayed: number;

  /** Number of matches this team has won */
  wins: number;

  /** Total points scored across all qualification matches */
  totalScore: number;

  /** Total penalty points assessed against this team */
  totalPenalties: number;

  /** Highest single-match score achieved by this team */
  highestScore: number;

  /** Official FRC ranking points used for seeding */
  rankingPoints: number;
}

/**
 * Validation functions for RankingEntry data integrity.
 */
export class RankingEntryValidators {
  /**
   * Validates that a ranking entry has all required fields with valid values.
   */
  static isValid(entry: RankingEntry): boolean {
    return entry &&
      typeof entry.teamId === 'string' &&
      entry.teamId.trim().length > 0 &&
      Number.isInteger(entry.matchesPlayed) && entry.matchesPlayed >= 0 &&
      Number.isInteger(entry.wins) && entry.wins >= 0 &&
      Number.isInteger(entry.totalScore) && entry.totalScore >= 0 &&
      Number.isInteger(entry.totalPenalties) && entry.totalPenalties >= 0 &&
      Number.isInteger(entry.highestScore) && entry.highestScore >= 0 &&
      Number.isInteger(entry.rankingPoints) && entry.rankingPoints >= 0;
  }

  /**
   * Validates that ranking entry relationships are consistent.
   * For example, wins cannot exceed matches played.
   */
  static isConsistent(entry: RankingEntry): boolean {
    return entry.wins <= entry.matchesPlayed &&
      entry.highestScore <= entry.totalScore &&
      entry.totalScore >= 0 &&
      entry.totalPenalties >= 0;
  }
}
