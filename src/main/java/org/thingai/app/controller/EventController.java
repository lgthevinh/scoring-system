package org.thingai.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thingai.app.controller.utils.ResponseEntityUtil;
import org.thingai.app.scoringservice.ScoringService;
import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.entity.event.Event;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @GetMapping("/")
    public ResponseEntity<Object> listEvents() {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.eventHandler().listEvents(new RequestCallback<Event[]>() {
            @Override
            public void onSuccess(Event[] responseObject, String message) {
                future.complete(ResponseEntity.ok().body(responseObject));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(ResponseEntity.status(500).body("Error listing events: " + errorMessage));
            }
        });
        return ResponseEntityUtil.getObjectResponse(future);
    }

    @GetMapping("/{eventCode}")
    public ResponseEntity<Object> getEvent(@PathVariable String eventCode) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.eventHandler().getEventByCode(eventCode, new RequestCallback<Event>() {
            @Override
            public void onSuccess(Event responseObject, String message) {
                future.complete(ResponseEntity.ok().body(responseObject));
            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(ResponseEntity.status(500).body("Error retrieving event: " + errorMessage));
            }
        });
        return ResponseEntityUtil.getObjectResponse(future);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createEvent(@RequestBody Event event) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.eventHandler().createEvent(event, new RequestCallback<Event>() {
            @Override
            public void onSuccess(Event responseObject, String message) {
                future.complete(ResponseEntity.ok().body(responseObject));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(ResponseEntity.status(500).body("Error creating event: " + errorMessage));
            }
        });
        return ResponseEntityUtil.getObjectResponse(future);
    }

    @PostMapping("/set")
    public ResponseEntity<Object> setupEvent(@RequestBody String eventCode) {
        CompletableFuture<ResponseEntity<Object>> future = new CompletableFuture<>();
        ScoringService.eventHandler().setSystemEvent(eventCode, new RequestCallback<Event>() {
            @Override
            public void onSuccess(Event responseObject, String message) {
                future.complete(ResponseEntity.ok().body(responseObject));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                future.complete(ResponseEntity.status(500).body("Error setting up event: " + errorMessage));
            }
        });

        return ResponseEntityUtil.getObjectResponse(future);
    }

}
