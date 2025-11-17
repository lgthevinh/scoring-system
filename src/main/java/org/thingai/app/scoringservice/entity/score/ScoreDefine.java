package org.thingai.app.scoringservice.entity.score;

import org.thingai.app.scoringservice.define.EUiType;
import org.thingai.app.scoringservice.define.EValueType;

public class ScoreDefine {
    public final String displayName;
    public final EUiType uiType;
    public final EValueType valueType;

    public ScoreDefine(String displayName, EUiType uiType, EValueType valueType) {
        this.displayName = displayName;
        this.uiType = uiType;
        this.valueType = valueType;
    }
}
