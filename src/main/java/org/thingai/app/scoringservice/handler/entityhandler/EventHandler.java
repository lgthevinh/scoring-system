package org.thingai.app.scoringservice.handler.entityhandler;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.ErrorCode;
import org.thingai.app.scoringservice.entity.config.DbMapEntity;
import org.thingai.app.scoringservice.entity.event.Event;
import org.thingai.app.scoringservice.entity.match.AllianceTeam;
import org.thingai.app.scoringservice.entity.match.Match;
import org.thingai.app.scoringservice.entity.ranking.RankingEntry;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.entity.team.Team;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFile;
import org.thingai.base.dao.DaoSqlite;

public class EventHandler {
    private static final String TAG = "EventHandler";

    private static Dao systemDao;
    private Dao eventDao;
    private DaoFile eventDaoFile;

    private Event currentEvent;

    public EventHandler(Dao dao) {
        this.systemDao = dao;
    }

    public void createEvent(Event event, RequestCallback<Event> callback) {
        try {
            systemDao.insertOrUpdate(event);
            this.currentEvent = event;
        } catch (Exception e) {
            callback.onFailure(ErrorCode.CREATE_FAILED, "Failed to create event: " + e.getMessage());
            return;
        }
        callback.onSuccess(event, "Event created successfully");
    }

    public void setupCurrentEvent(String eventCode, RequestCallback<Event> callback) {
        try {
            Event[] events = systemDao.query(Event.class, new String[]{"eventCode"}, new String[]{eventCode});
            if (events.length == 0) {
                callback.onFailure(ErrorCode.NOT_FOUND, "Event with code " + eventCode + " not found.");
                return;
            }
            this.currentEvent = events[0];
            eventDao = new DaoSqlite(this.currentEvent.getEventCode() + ".db");
            eventDao.initDao(new Class[]{
                    Match.class,
                    AllianceTeam.class,
                    Team.class,
                    Score.class,
                    RankingEntry.class,
            });

            // init new folder for event files
            eventDaoFile = new DaoFile("files/" + this.currentEvent.getEventCode());

            // store current event code in system dao for persistence
            DbMapEntity mapEntity = new DbMapEntity();
            mapEntity.setKey("current_event");
            mapEntity.setValue(eventCode);
            systemDao.insertOrUpdate(mapEntity);

            callback.onSuccess(this.currentEvent, "Current event set successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Error setting current event: " + e.getMessage());
        }
    }

    public boolean isCurrentEventSet() {
        DbMapEntity[] mapEntities = systemDao.query(DbMapEntity.class, "key", "current_event"); // this get event code
        if (mapEntities.length > 0) {
            String eventCode = mapEntities[0].getValue();
            try {
                Event[] events = systemDao.query(Event.class, new String[]{"eventCode"}, new String[]{eventCode});
                if (events.length > 0) {
                    this.currentEvent = events[0];
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return this.currentEvent != null;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public Dao getEventDao() {
        return eventDao;
    }

    public DaoFile getEventDaoFile() {
        return eventDaoFile;
    }
}
