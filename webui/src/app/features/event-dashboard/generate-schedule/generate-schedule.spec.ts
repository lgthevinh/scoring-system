import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateSchedule } from './generate-schedule';

describe('GenerateSchedule', () => {
  let component: GenerateSchedule;
  let fixture: ComponentFixture<GenerateSchedule>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenerateSchedule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenerateSchedule);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
