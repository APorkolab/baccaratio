import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BetPanelComponent } from './bet-panel.component';

describe('BetPanelComponent', () => {
  let component: BetPanelComponent;
  let fixture: ComponentFixture<BetPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BetPanelComponent], 
    }).compileComponents();

    fixture = TestBed.createComponent(BetPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
