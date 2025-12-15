import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventService } from '../../core/services/event.service';
import { Event } from '../../core/models/event.model';
import { Observable } from 'rxjs';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-home.component',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './home.html',
    styleUrl: './home.css'
})
export class Home implements OnInit {
    currentEvent$: Observable<Event | null>;

    constructor(private eventService: EventService) {
        this.currentEvent$ = this.eventService.currentEvent$;
    }

    ngOnInit() {
        this.eventService.getCurrentEvent().subscribe();
    }
}
