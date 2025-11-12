import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RedAlliance } from './red-alliance';

describe('RedAlliance', () => {
  let component: RedAlliance;
  let fixture: ComponentFixture<RedAlliance>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RedAlliance]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RedAlliance);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
