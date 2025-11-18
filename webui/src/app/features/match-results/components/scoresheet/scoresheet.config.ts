export interface ScoresheetConfig {
    periods: PeriodConfig[];
}

export interface PeriodConfig {
    id: string;
    title: string;
    sections: SectionConfig[];
}

export interface SectionConfig {
    id: string;
    title?: string;
    type: 'fields' | 'team-table';
    fields?: FieldConfig[];
    columns?: ColumnConfig[];
}

export interface FieldConfig {
    key: string; // Path in JSON, e.g., "auto.samples.highBasket"
    label: string;
    type: 'number' | 'boolean' | 'text';
}

export interface ColumnConfig {
    key: string; // Path relative to team entry, e.g., "robot"
    label: string;
    type: 'checkbox' | 'text' | 'enum';
    options?: string[];
}

export const CUSTOM_SEASON_CONFIG: ScoresheetConfig = {
    periods: [
        {
            id: 'main',
            title: 'Match Score',
            sections: [
                {
                    id: 'scoring',
                    title: 'Scoring',
                    type: 'fields',
                    fields: [
                        { key: 'robotParked', label: 'Robots Parked', type: 'number' },
                        { key: 'robotHanged', label: 'Robots Hanged', type: 'number' },
                        { key: 'ballEntered', label: 'Balls Entered', type: 'number' }
                    ]
                },
                {
                    id: 'fouls',
                    title: 'Fouls',
                    type: 'fields',
                    fields: [
                        { key: 'minorFault', label: 'Minor Faults', type: 'number' },
                        { key: 'majorFault', label: 'Major Faults', type: 'number' }
                    ]
                }
            ]
        }
    ]
};
