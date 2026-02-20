import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpNextDisplay } from './up-next-display';

describe('UpNextDisplay', () => {
  let component: UpNextDisplay;
  let fixture: ComponentFixture<UpNextDisplay>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpNextDisplay]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpNextDisplay);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
