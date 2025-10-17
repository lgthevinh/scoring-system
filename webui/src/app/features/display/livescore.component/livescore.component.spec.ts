import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LivescoreComponent } from './livescore.component';

describe('LivescoreComponent', () => {
  let component: LivescoreComponent;
  let fixture: ComponentFixture<LivescoreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LivescoreComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LivescoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
