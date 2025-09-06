import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GameTableComponent } from './game-table.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('GameTableComponent', () => {
  let component: GameTableComponent;
  let fixture: ComponentFixture<GameTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameTableComponent,
               HttpClientTestingModule
               ],
    }).compileComponents();

    fixture = TestBed.createComponent(GameTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
