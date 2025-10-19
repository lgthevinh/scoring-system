package org.thingai.app.scoringservice.handler;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.ErrorCode;
import org.thingai.app.scoringservice.entity.config.DbMapEntity;
import org.thingai.app.scoringservice.entity.event.Event;
import org.thingai.base.dao.Dao;

public class EventHandler {
    private final Dao dao;
    private final Dao daoFile;

    public EventHandler(Dao dao, Dao daoFile) {
        this.dao = dao;
        this.daoFile = daoFile;
    }

    public void createEvent(Event event, RequestCallback<Event> callback) {
        try {
            dao.insert(Event.class, event);
            callback.onSuccess(event, "Event created successfully");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.CREATE_FAILED,"Failed to create event: " + e.getMessage());
        }
    }

    public void getEventById(String eventId, RequestCallback<Event> callback) {
        try {
            Event event = dao.read(Event.class, eventId);
            if (event != null) {
                callback.onSuccess(event, "Event retrieved successfully");
            } else {
                callback.onFailure(ErrorCode.NOT_FOUND, "Event not found");
            }
        } catch (Exception e) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to retrieve event: " + e.getMessage());
        }
    }

    public void updateEvent(Event event, RequestCallback<Event> callback) {
        try {
            dao.update(Event.class, event.getUuid(), event);
            callback.onSuccess(event, "Event updated successfully");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.UPDATE_FAILED, "Failed to update event: " + e.getMessage());
        }
    }

    public void deleteEvent(String eventId, RequestCallback<Void> callback) {
        try {
            dao.delete(Event.class, eventId);
            callback.onSuccess(null, "Event deleted successfully");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.DELETE_FAILED, "Failed to delete event: " + e.getMessage());
        }
    }

    public void setSystemEvent(String eventId, RequestCallback<Void> callback) {
        try {
            DbMapEntity dbMapEntity = new DbMapEntity();
            dbMapEntity.setKey("current_event");
            dbMapEntity.setValue(eventId);
            dao.insertOrUpdate(DbMapEntity.class, dbMapEntity);
            callback.onSuccess(null, "System event set successfully");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.UPDATE_FAILED, "Failed to set system event: " + e.getMessage());
        }
    }
    public void syncEventFromCloud(String eventId, RequestCallback<Event> callback) {

    }
}
