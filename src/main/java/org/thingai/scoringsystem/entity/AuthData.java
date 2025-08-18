package org.thingai.scoringsystem.entity;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "authen_data")
public class AuthData {
    @DaoColumn(name = "username", primaryKey = true, unique = true)
    private String username;

    @DaoColumn(nullable = false)
    private String password;

    @DaoColumn(nullable = false)
    private String salt;
}
