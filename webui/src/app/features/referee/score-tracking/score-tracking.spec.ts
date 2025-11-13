import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScoreTracking } from './score-tracking';

describe('ScoreTracking', () => {
  let component: ScoreTracking;
  let fixture: ComponentFixture<ScoreTracking>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScoreTracking]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScoreTracking);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
