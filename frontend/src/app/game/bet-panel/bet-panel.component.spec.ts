import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BetPanelComponent } from './bet-panel.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ToastrModule } from 'ngx-toastr';

describe('BetPanelComponent', () => {
  let component: BetPanelComponent;
  let fixture: ComponentFixture<BetPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BetPanelComponent, HttpClientTestingModule, ToastrModule.forRoot()], 
    }).compileComponents();

    fixture = TestBed.createComponent(BetPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
