package com.thingai;

import com.thingai.database.DatabaseFactory;
import com.thingai.database.IDatabase;
import com.thingai.model.team.Team;

public class Main {
    public static void main(String[] args) {
        IDatabase<Team> teamDatabase = DatabaseFactory.getDatabaseInstance(Team.class);
    }
}