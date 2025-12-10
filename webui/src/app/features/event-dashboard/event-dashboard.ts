import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventService } from '../../core/services/event.service';
import { Event as GameEvent } from '../../core/models/event.model';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-event-dashboard',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './event-dashboard.html',
  styleUrl: './event-dashboard.css'
})
export class EventDashboard implements OnInit {
  tabs: string[] = ['All Events', 'Event Tools'];
  activeTab: string = 'All Events';

  events: GameEvent[] = [];
  isEditing: boolean = false;
  currentEditEvent: GameEvent | null = null;
  showForm: boolean = false;

  // Form model
  formEvent: GameEvent = {
    uuid: '',
    name: '',
    eventCode: '',
    fieldCount: 3,
    date: '',
    location: '',
    description: '',
    website: '',
    organizer: ''
  };

  constructor(private eventService: EventService) { }

  ngOnInit() {
    this.loadEvents();
  }

  loadEvents() {
    this.eventService.listEvents().subscribe(events => {
      this.events = events;
    });
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  openCreateForm() {
    this.isEditing = false;
    this.currentEditEvent = null;
    this.resetForm();
    this.showForm = true;
  }

  openEditForm(event: GameEvent) {
    this.isEditing = true;
    this.currentEditEvent = event;
    this.formEvent = { ...event };
    this.showForm = true;
  }

  cancelForm() {
    this.showForm = false;
    this.resetForm();
  }

  resetForm() {
    this.formEvent = {
      uuid: '',
      name: '',
      eventCode: '',
      fieldCount: 3,
      date: '',
      location: '',
      description: '',
      website: '',
      organizer: ''
    };
  }

  saveEvent() {
    if (this.isEditing) {
      this.eventService.updateEvent(this.formEvent).subscribe(() => {
        this.loadEvents();
        this.showForm = false;
        alert('Event updated successfully');
      }, err => alert('Failed to update event: ' + err.message));
    } else {
      // Generate UUID if needed or backend handles it? Backend seems to expect it or generate it.
      // Assuming backend generates or we generate. The model has it.
      // For now let's leave uuid empty, backend might handle it or we might need a util.
      // Given existing code, let's assume backend handles if empty or we need to generate.
      // Looking at backend `createEvent` -> `systemDao.insertOrUpdate(event)`. if UUID is key, it needs it.
      this.formEvent.uuid = crypto.randomUUID();

      this.eventService.createEvent(this.formEvent).subscribe(() => {
        this.loadEvents();
        this.showForm = false;
        alert('Event created successfully');
      }, err => alert('Failed to create event: ' + err.message));
    }
  }

  deleteEvent(event: GameEvent) {
    if (confirm(`Are you sure you want to delete event "${event.name}"?`)) {
      this.eventService.deleteEvent(event.eventCode).subscribe(() => {
        this.loadEvents();
      }, err => alert('Failed to delete event: ' + err.message));
    }
  }

  setSystemEvent(event: GameEvent) {
    if (confirm(`Set "${event.name}" as the current system event?`)) {
      this.eventService.setSystemEvent(event.eventCode).subscribe(() => {
        alert(`Current event set to ${event.name}`);
      }, err => alert('Failed to set system event: ' + err.message));
    }
  }
}
