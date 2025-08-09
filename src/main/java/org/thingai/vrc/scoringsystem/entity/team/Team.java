package org.thingai.vrc.scoringsystem.entity.team;

import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "team")
public class Team {
    private int teamId;
    private String teamName;
    private String teamSchool;

}
