package org.thingai.app.scoringservice.callback;

public interface RequestCallback<T> {
    void onSuccess(T responseObject, String message);
    void onFailure(int errorCode, String errorMessage);
}
