import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventDashboard } from './event-dashboard';

describe('EventDashboard', () => {
  let component: EventDashboard;
  let fixture: ComponentFixture<EventDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
