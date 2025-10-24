import { Injectable, OnDestroy } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { Subject, Observable, BehaviorSubject } from 'rxjs'; // Import BehaviorSubject
import { BroadcastMessage } from '../models/broadcast.model';

@Injectable({
  providedIn: 'root'
})
export class LiveScoreService implements OnDestroy {
  private client: Client;
  private messagesSubject = new Subject<BroadcastMessage>();
  public messages$: Observable<BroadcastMessage> = this.messagesSubject.asObservable();
  // Use BehaviorSubject to track connection state and allow late subscribers
  private connectionState = new BehaviorSubject<boolean>(false);

  constructor() {
    this.client = new Client({
      brokerURL: 'ws://localhost:9090/ws', // Single endpoint
      debug: (str) => { console.log(new Date(), str); },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = (frame) => {
      console.log('STOMP: Connected');
      this.connectionState.next(true); // Signal successful connection
    };

    this.client.onStompError = (frame) => {
      console.error('STOMP: Broker reported error: ' + frame.headers['message']);
      console.error('STOMP: Additional details: ' + frame.body);
      this.connectionState.next(false);
    };

    this.client.onWebSocketClose = () => {
      console.log("STOMP: WebSocket connection closed.");
      this.connectionState.next(false);
    };

    this.client.onWebSocketError = (error) => {
      console.error("STOMP: WebSocket error:", error);
      this.connectionState.next(false);
      // Attempt reconnection
      if (!this.client.active) {
        this.client.activate();
      }
    }

    this.client.activate();
  }

  // Generic internal subscription logic
  private subscribeToTopicInternal(topic: string) {
    this.client.subscribe(topic, (message: IMessage) => {
      try {
        const parsedMessage: BroadcastMessage = JSON.parse(message.body);
        this.messagesSubject.next(parsedMessage);
      } catch (e) {
        console.error(`Failed to parse incoming WebSocket message on ${topic}`, message.body, e);
      }
    });
    console.log(`STOMP: Subscribed to ${topic}`);
  }


  // Public method for components to subscribe dynamically
  subscribeToTopic(topic: string): Observable<BroadcastMessage> {
    const topicSubject = new Subject<BroadcastMessage>();
    // Only subscribe if connected, handle late subscription
    this.connectionState.subscribe(isConnected => {
      if (isConnected && this.client.connected) {
        const subscription = this.client.subscribe(topic, (message: IMessage) => {
          try {
            topicSubject.next(JSON.parse(message.body));
          } catch (e) { topicSubject.error(e); }
        });
        console.log(`Subscribed to ${topic}`);
        // When the topicSubject is unsubscribed from, clean up the STOMP subscription
        return () => {
          if (subscription) {
            subscription.unsubscribe();
            console.log(`Unsubscribed from ${topic}`);
          }
        }
      }
      return undefined;
    });
    return topicSubject.asObservable();
  }

  ngOnDestroy() {
    if (this.client.active) {
      this.client.deactivate().then(() => {
        console.log("STOMP: Client deactivated on destroy.");
      });
    }
    this.connectionState.complete();
    this.messagesSubject.complete();
  }
}

