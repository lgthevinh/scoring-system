import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScoringDisplay } from './scoring-display';

describe('ScoringDisplay', () => {
  let component: ScoringDisplay;
  let fixture: ComponentFixture<ScoringDisplay>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScoringDisplay]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScoringDisplay);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
