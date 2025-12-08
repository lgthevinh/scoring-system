export enum EUiType {
    COUNTER = 1,
    TOGGLE = 2
}

export enum EValueType {
    INTEGER = 1,
    BOOLEAN = 2
}

export interface ScoreDefine {
    displayName: string;
    uiType: EUiType;
    valueType: EValueType;
}
