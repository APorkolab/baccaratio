import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PlayerStatusComponent } from './player-status.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { GameService } from '../game.service';
import { AuthService } from 'src/app/auth/auth.service';
import { Router } from '@angular/router';

describe('PlayerStatusComponent', () => {
  let component: PlayerStatusComponent;
  let fixture: ComponentFixture<PlayerStatusComponent>;

  // 🔧 Mock szolgáltatások
  const gameServiceMock = {
    getGameState: jasmine.createSpy('getGameState').and.returnValue(of({ playerChips: 500 })),
    balance$: of(500),
    resetGame: jasmine.createSpy('resetGame').and.returnValue(of(null))
  };

  const authServiceMock = {
    logout: jasmine.createSpy('logout')
  };

  const routerMock = {
    navigate: jasmine.createSpy('navigate')
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        PlayerStatusComponent,
        HttpClientTestingModule // a GameService belső HTTP-hívásai miatt
      ],
      providers: [
        { provide: GameService, useValue: gameServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PlayerStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set initial balance from getGameState()', () => {
    expect(component.balance).toBe(500);
  });

  it('should call logout and navigate on logout()', () => {
    component.logout();
    expect(authServiceMock.logout).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should call gameService.resetGame() on resetGame()', () => {
    component.resetGame();
    expect(gameServiceMock.resetGame).toHaveBeenCalled();
  });

  it('should update currentBetAmount', () => {
    component.updateCurrentBetAmount(123);
    expect(component.currentBetAmount).toBe(123);
  });
});
