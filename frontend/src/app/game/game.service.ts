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

  // A fogadások helyezése
  placeBet(type: string, amount: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/bet/${type}/${amount}`, {}).pipe(
      switchMap(response => this.getPlayer()),
      catchError((error: HttpErrorResponse) => {
        console.error('Error placing bet:', error);
        return throwError(() => new Error('Failed to place bet. Please try again.'));
      })
    );
  }

  // Egy játék lejátszása
  playGame(): Observable<string> {
    return this.http.get<string>(`${this.apiUrl}/play`, {
      responseType: 'text' as 'json',
    });
  }

  // A játékos és bankár kártyáinak lekérése
  getCards(): Observable<{ playerCards: Card[], bankerCards: Card[] }> {
    this.isLoading = true;
    return this.http.get<{ playerCards: Card[], bankerCards: Card[] }>(`${this.apiUrl}/cards`).pipe(
      tap((response) => {
        console.log('Kártyák lekérve:', response);
      }),
      finalize(() => {
        setTimeout(() => {
          this.isLoading = false;
        }, 7000);
      }),
      catchError((error: any) => {
        console.error('Hiba történt a kártyák lekérése közben:', error);
        this.isLoading = false;
        return throwError(() => new Error('Failed to get cards'));
      })
    );
  }

  // Az utolsó játék eredményének lekérése
  getLastResult(): Observable<string> {
    return this.http.get<string>(`${this.apiUrl}/result`, {
      responseType: 'text' as 'json',
    });
  }

  getPlayer(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/player`).pipe(
      tap((response) => {
        if (response.chips) {
          this.updateBalance(response.chips);
        }
      })
    );
  }

  // // Az egyenleg frissítése a tét fogadás után
  // updateTotalBet(betAmount: number): void {
  //   console.log(`Total bet updated: ${betAmount}`);
  //   // Itt nem küldünk kérést a szervernek, csak lokálisan frissítjük az egyenleget
  //   const currentBalance = this.balanceSubject.getValue();
  //   this.updateBalance(currentBalance - betAmount);
  // }

  // Az egyenleg frissítése a zsetonok módosítása után
  updateChips(amount: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/player/chips`, { amount }).pipe(
      tap({
        next: (response) => {
          this.updateBalance(this.balanceSubject.getValue() + amount); // Frissítse az egyenleget
          console.log('Chips updated:', response);
        },
        error: (error) => console.error('Error updating chips:', error),
      })
    );
  }


  getPlayerName(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/player/name`);
  }

  setPlayerName(name: string): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/player/name`, {
      name,
    });
  }

  getChips(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/player/chips`);
  }

  updateBalance(newBalance: number): void {
    this.balanceSubject.next(newBalance);
  }

  updateBalanceOnServer(amount: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/player/chips`, { amount }).pipe(
      tap({
        next: (response) => {
          console.log('Mérleg frissítve: ', response.message);
          this.updateBalance(this.balanceSubject.getValue() + amount);
        },
        error: (error) => {
          console.error('Hiba a mérleg frissítése közben: ', error);
        },
      })
    );
  }
}
