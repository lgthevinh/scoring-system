import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlueAlliance } from './blue-alliance';

describe('BlueAlliance', () => {
  let component: BlueAlliance;
  let fixture: ComponentFixture<BlueAlliance>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BlueAlliance]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BlueAlliance);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
