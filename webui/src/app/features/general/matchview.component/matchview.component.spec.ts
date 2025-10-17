import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchviewComponent } from './matchview.component';

describe('MatchviewComponent', () => {
  let component: MatchviewComponent;
  let fixture: ComponentFixture<MatchviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatchviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MatchviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
