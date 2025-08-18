package org.thingai.scoringsystem.entity;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "db_map")
public class DbMap {
    @DaoColumn(primaryKey = true, unique = true)
    private String key;

    @DaoColumn(nullable = false)
    private String value;
}
