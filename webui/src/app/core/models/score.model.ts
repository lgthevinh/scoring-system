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

export enum EUiType {
  COUNTER = 'COUNTER',
  TOGGLE = 'TOGGLE',
  TEXT = 'TEXT'
}

export enum EValueType {
  NUMBER = 'NUMBER',
  BOOLEAN = 'BOOLEAN',
  STRING = 'STRING'
}

export interface ScoreDefine {
  key: string;
  displayName: string;
  uiType: EUiType;
  valueType: EValueType;
  min?: number;
  max?: number;
  step?: number;
  defaultValue?: any;
  order?: number;
}
