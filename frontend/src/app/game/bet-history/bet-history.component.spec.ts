import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BetHistoryComponent } from './bet-history.component';

describe('BetHistoryComponent', () => {
  let component: BetHistoryComponent;
  let fixture: ComponentFixture<BetHistoryComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BetHistoryComponent]
    });
    fixture = TestBed.createComponent(BetHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
