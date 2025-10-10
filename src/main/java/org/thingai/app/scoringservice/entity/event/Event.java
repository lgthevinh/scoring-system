package org.thingai.app.scoringservice.entity.event;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "event")
public class Event {
    @DaoColumn(name = "uuid", primaryKey = true)
    private String uuid;

    @DaoColumn(name = "name")
    private String name;

    @DaoColumn(name = "date")
    private String date;

    @DaoColumn(name = "location")
    private String location;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
