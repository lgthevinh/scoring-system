package org.thingai.scoringsystem;

import org.thingai.base.Service;
import org.thingai.scoringsystem.entity.AuthData;
import org.thingai.scoringsystem.entity.DbMap;
import org.thingai.scoringsystem.entity.match.Match;
import org.thingai.scoringsystem.entity.match.MatchAlliance;
import org.thingai.scoringsystem.entity.team.Team;

public class ScoringService extends Service {
    @Override
    public void onServiceInit() {
        this.facDao(new Class[]{
                Match.class,
                MatchAlliance.class,
                // ScoreSeasonDemo.class,
                Team.class,
                AuthData.class,
                DbMap.class
        });

        System.out.println("Service initialized with app directory: " + appDir);
    }

    @Override
    public void run() {
    }
}
