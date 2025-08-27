import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, map, catchError, switchMap, finalize } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Card } from '../model/card';
import { Player } from '../model/player';
import { GameResponse } from '../model/dto/game-response.dto';
import { BetResponse } from '../model/dto/bet-response.dto';
import { PlayerDTO } from '../model/dto/player.dto';

@Injectable({
  providedIn: 'root',
})
export class GameService {
  apiUrl: string = environment.apiUrl;
  private playerCardsSubject = new BehaviorSubject<Card[]>([]);
  private bankerCardsSubject = new BehaviorSubject<Card[]>([]);
  private balanceSubject = new BehaviorSubject<number>(1000);
  balance$ = this.balanceSubject.asObservable();
  isLoading: boolean = false;

  constructor(private http: HttpClient) { }

  playerCards$ = this.playerCardsSubject.asObservable();
  bankerCards$ = this.bankerCardsSubject.asObservable();

  placeBet(type: string, amount: number): Observable<BetResponse> {
    return this.http.post<BetResponse>(`${this.apiUrl}/baccarat/bet/${type}/${amount}`, {}).pipe(
      tap(response => this.updateBalance(response.chips)),
      catchError(this.handleError)
    );
  }

  playGame(): Observable<GameResponse> {
    return this.http.get<GameResponse>(`${this.apiUrl}/baccarat/play`).pipe(
      tap(response => {
        this.updateCards(response.playerCards, response.bankerCards);
        this.updateBalance(response.playerChips);
      }),
      catchError(this.handleError)
    );
  }

  getGameState(): Observable<GameResponse> {
    return this.http.get<GameResponse>(`${this.apiUrl}/baccarat/state`).pipe(
      tap(response => {
        this.updateCards(response.playerCards, response.bankerCards);
        this.updateBalance(response.playerChips);
      }),
      catchError(this.handleError)
    );
  }

  resetGame(): Observable<GameResponse> {
    return this.http.post<GameResponse>(`${this.apiUrl}/baccarat/reset`, {}).pipe(
        tap(response => {
            this.updateCards(response.playerCards, response.bankerCards);
            this.updateBalance(response.playerChips);
        }),
        catchError(this.handleError)
    );
  }

  getPlayer(): Observable<PlayerDTO> {
    // This endpoint might be deprecated in favor of getGameState, but we'll keep it for now
    return this.http.get<PlayerDTO>(`${this.apiUrl}/baccarat/player`).pipe(
      tap(response => this.updateBalance(response.chips)),
      catchError(this.handleError)
    );
  }

  private updateBalance(newBalance: number): void {
    this.balanceSubject.next(newBalance);
  }

  private updateCards(playerCards: Card[], bankerCards: Card[]): void {
    this.playerCardsSubject.next(playerCards);
    this.bankerCardsSubject.next(bankerCards);
  }

  private handleError(error: HttpErrorResponse) {
    console.error('An error occurred:', error.error.message || error.statusText);
    return throwError(() => new Error('Something bad happened; please try again later.'));
  }
}
