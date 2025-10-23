import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LiveDisplayComponent } from './live-display.component';

describe('LiveDisplayComponent', () => {
  let component: LiveDisplayComponent;
  let fixture: ComponentFixture<LiveDisplayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LiveDisplayComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LiveDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
