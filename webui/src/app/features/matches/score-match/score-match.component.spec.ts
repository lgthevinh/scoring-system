import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScoreMatchComponent } from './score-match.component';

describe('ScoreMatchComponent', () => {
  let component: ScoreMatchComponent;
  let fixture: ComponentFixture<ScoreMatchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScoreMatchComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScoreMatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
