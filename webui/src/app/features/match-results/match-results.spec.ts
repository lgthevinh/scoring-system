import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchResults } from './match-results';

describe('MatchResults', () => {
  let component: MatchResults;
  let fixture: ComponentFixture<MatchResults>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatchResults]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MatchResults);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
