import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BetHistoryComponent } from './bet-history.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('BetHistoryComponent', () => {
  let component: BetHistoryComponent;
  let fixture: ComponentFixture<BetHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BetHistoryComponent, HttpClientTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(BetHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
