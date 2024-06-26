import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

import { Card } from '../model/card';

@Injectable({
  providedIn: 'root',
})
export class GameService {
  apiUrl: string = environment.apiUrl;
  private playerCardsSubject = new BehaviorSubject<Card[]>([]);
  private bankerCardsSubject = new BehaviorSubject<Card[]>([]);
  private balanceSubject = new BehaviorSubject<number>(1000);
  balance$ = this.balanceSubject.asObservable();

  constructor(private http: HttpClient) { }

  playerCards$ = this.playerCardsSubject.asObservable();
  bankerCards$ = this.bankerCardsSubject.asObservable();

  // A fogadások helyezése
  placeBet(type: string, amount: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/bet/${type}/${amount}`, {}).pipe(
      switchMap(response => {
        // Tét helyezés sikeres, most lekérdezzük a játékos adatait
        return this.getPlayer();
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
  getCards(): Observable<{ playerCards: Card[]; bankerCards: Card[] }> {
    return this.http
      .get<{ player: Card[]; banker: Card[] }>(`${this.apiUrl}/cards`)
      .pipe(
        tap((response) => console.log('Backend response:', response)),
        map((response) => ({
          playerCards: response.player,
          bankerCards: response.banker,
        }))
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

  // Az egyenleg frissítése a tét fogadás után
  updateTotalBet(betAmount: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/player/bet`, { amount: betAmount }).pipe(
      tap({
        next: (response) => {
          this.updateBalance(this.balanceSubject.getValue() - betAmount); // Frissítse az egyenleget
          console.log('Total bet updated:', response);
        },
        error: (error) => console.error('Error updating total bet:', error),
      })
    );
  }

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

  updateBalanceOnServer(newBalance: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/player/chips`, { newBalance }).pipe(
      tap({
        next: (response) => {
          console.log('Egyensúly frissítve: ', response.message);
          this.updateBalance(newBalance); // Frissítsük a helyi egyenleget is
        },
        error: (error) => {
          console.error('Hiba az egyensúly frissítése közben: ', error);
        },
      })
    );
  }
}
