import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchControl } from './match-control';

describe('MatchControl', () => {
  let component: MatchControl;
  let fixture: ComponentFixture<MatchControl>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatchControl]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MatchControl);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
