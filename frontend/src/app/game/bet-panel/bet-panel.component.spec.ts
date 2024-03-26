import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BetPanelComponent } from './BetPanelComponent';

describe('BetPanelComponent', () => {
  let component: BetPanelComponent;
  let fixture: ComponentFixture<BetPanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BetPanelComponent]
    });
    fixture = TestBed.createComponent(BetPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
