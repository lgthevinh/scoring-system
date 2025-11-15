export interface Score {
  id: string;
  status: number;
  totalScore: number;
  penaltiesScore: number;
  rawScoreData: string; // JSON string

  // From fromJson
  robotParked?: number;
  robotHanged?: number;
  ballEntered?: number;
  minorFault?: number;
  majorFault?: number;
}

export interface CustomScoreData {
  [key: string]: number | boolean;
}
