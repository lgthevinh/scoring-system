package org.thingai.scoringsystem.entity.team;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "team")
public class Team {
    @DaoColumn(name = "id", primaryKey = true)
    private int teamId;

    @DaoColumn(name = "name")
    private String teamName;

    @DaoColumn(name = "number")
    private String teamSchool;

    @DaoColumn(name = "region")
    private String teamRegion;

}
