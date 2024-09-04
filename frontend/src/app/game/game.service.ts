import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, map, catchError, switchMap, finalize } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Card } from '../model/card';
import { Player } from '../model/player';

@Injectable({
  providedIn: 'root',
})
export class GameService {
  [x: string]: any;
  apiUrl: string = environment.apiUrl;
  private playerCardsSubject = new BehaviorSubject<Card[]>([]);
  private bankerCardsSubject = new BehaviorSubject<Card[]>([]);
  private balanceSubject = new BehaviorSubject<number>(1000);
  balance$ = this.balanceSubject.asObservable();
  isLoading: boolean = false;

  constructor(private http: HttpClient) { }

  playerCards$ = this.playerCardsSubject.asObservable();
  bankerCards$ = this.bankerCardsSubject.asObservable();


  placeBet(type: string, amount: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/bet/${type}/${amount}`, {}).pipe(
      switchMap(() => this.getPlayer()),
      catchError((error: HttpErrorResponse) => {
        console.error('Error placing bet:', error);
        return throwError(() => new Error('Failed to place bet. Please try again.'));
      })
    );
  }

  playGame(): Observable<{ result: string; cards: { playerCards: Card[]; bankerCards: Card[] } }> {
    return this.http.get(`${this.apiUrl}/play`, { responseType: 'text' }).pipe(
      switchMap((result: string) => this.getCards().pipe(
        map(cards => ({ result, cards }))
      )),
      tap(response => {
        this.updateCards(response.cards.playerCards, response.cards.bankerCards);
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Error during game play:', error.message, 'Status:', error.status);
        return throwError(() => new Error(`Failed to play game. Status: ${error.status}. Message: ${error.message}`));
      })
    );
  }

  getCards(): Observable<{ playerCards: Card[]; bankerCards: Card[] }> {
    this.isLoading = true;
    return this.http.get<{ playerCards: Card[]; bankerCards: Card[] }>(`${this.apiUrl}/cards`).pipe(
      tap(response => {
        console.log('Kártyák lekérve:', response);
      }),
      finalize(() => {
        this.isLoading = false;
      }),
      catchError((error: any) => {
        console.error('Hiba történt a kártyák lekérése közben:', error);
        this.isLoading = false;
        return throwError(() => new Error('Failed to get cards'));
      })
    );
  }


  getLastResult(): Observable<string> {
    return this.http.get<string>(`${this.apiUrl}/result`, {
      responseType: 'text' as 'json',
    }).pipe(
      catchError((error: any) => {
        console.error('Error getting last result:', error);
        return throwError(() => new Error('Failed to get last result.'));
      })
    );
  }

  getPlayer(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/player`).pipe(
      tap(response => {
        if (response.chips) {
          this.updateBalance(response.chips);
        }
      }),
      catchError((error: any) => {
        console.error('Error getting player data:', error);
        return throwError(() => new Error('Failed to get player data.'));
      })
    );
  }

  updateChips(amount: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/player/chips`, { amount }).pipe(
      tap(response => {
        console.log('Zsetonok frissítve:', response);
        if (response.chips) {
          this.updateBalance(response.chips);
        }
      }),
      catchError((error: any) => {
        console.error('Hiba a zsetonok frissítésekor:', error);
        return throwError(() => new Error('Nem sikerült frissíteni a zsetonokat.'));
      })
    );
  }

  getPlayerName(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/player/name`).pipe(
      catchError((error: any) => {
        console.error('Error getting player name:', error);
        return throwError(() => new Error('Failed to get player name.'));
      })
    );
  }

  setPlayerName(name: string): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/player/name`, { name }).pipe(
      catchError((error: any) => {
        console.error('Error setting player name:', error);
        return throwError(() => new Error('Failed to set player name.'));
      })
    );
  }

  getChips(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/player/chips`).pipe(
      catchError((error: any) => {
        console.error('Error getting chips:', error);
        return throwError(() => new Error('Failed to get chips.'));
      })
    );
  }

  updateBalance(newBalance: number): void {
    this.balanceSubject.next(newBalance);
  }

  updateBalanceOnServer(amount: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/player/chips`, { amount }).pipe(
      tap(response => {
        console.log('Mérleg frissítve: ', response.message);
        // Az új egyenleg felülírja a meglévőt
        this.updateBalance(amount);
      }),
      catchError((error: any) => {
        console.error('Hiba a mérleg frissítése közben: ', error);
        return throwError(() => new Error('Failed to update balance.'));
      })
    );
  }

  updateCards(playerCards: Card[], bankerCards: Card[]): void {
    this.playerCardsSubject.next(playerCards);
    this.bankerCardsSubject.next(bankerCards);
  }

  updatePlayerCards(card: Card): void {
    const currentCards = this.playerCardsSubject.getValue();
    this.playerCardsSubject.next([...currentCards, card]);
  }

  updateBankerCards(card: Card): void {
    const currentCards = this.bankerCardsSubject.getValue();
    this.bankerCardsSubject.next([...currentCards, card]);
  }


  dealHands(): Observable<{ playerHand: Card[]; bankerHand: Card[] }> {
    return this.http.get<{ playerHand: Card[]; bankerHand: Card[] }>(`${this.apiUrl}/play`).pipe(
      tap(response => {
        if (response.playerHand && response.bankerHand) {
          this.updateCards(response.playerHand, response.bankerHand);
        } else {
          console.warn('Nem érkezett kártya a szervertől vagy nincs aktív játék.');
        }
      }),
      catchError((error: any) => {
        if (error.status === 200 && error.error && typeof error.error === 'string') {
          console.warn(error.error);
          return throwError(() => new Error(error.error));
        }
        console.error('Hiba történt a kártyák osztása közben:', error);
        return throwError(() => new Error('Nem sikerült kártyákat osztani. Kérjük, először tegyen tétet.'));
      })
    );
  }
}
