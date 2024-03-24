import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

import { Card } from '../model/card';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private readonly apiUrl = 'http://localhost:8080/baccarat';
  private playerCardsSubject = new BehaviorSubject<Card[]>([]);
  private bankerCardsSubject = new BehaviorSubject<Card[]>([]);

  constructor(private http: HttpClient) { }

  playerCards$ = this.playerCardsSubject.asObservable();
  bankerCards$ = this.bankerCardsSubject.asObservable();
  // A fogadások helyezése
  placeBet(type: string, amount: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/bet/${type}/${amount}`, {});
  }

  // Egy játék lejátszása
  playGame(): Observable<string> {
    return this.http.get<string>(`${this.apiUrl}/play`, { responseType: 'text' as 'json' });
  }

  // A játékos és bankár kártyáinak lekérése
  getCards(): Observable<{ playerCards: Card[], bankerCards: Card[] }> {
    return this.http.get<{ player: Card[], banker: Card[] }>(`${this.apiUrl}/cards`).pipe(
      tap(response => console.log('Backend response:', response)),
      map(response => ({
        playerCards: response.player,
        bankerCards: response.banker
      }))
    );
  }



  // Az utolsó játék eredményének lekérése
  getLastResult(): Observable<string> {
    return this.http.get<string>(`${this.apiUrl}/result`, { responseType: 'text' as 'json' });
  }
}
