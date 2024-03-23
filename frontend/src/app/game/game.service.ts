import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Card } from '../model/card';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private readonly apiUrl = 'http://localhost:8080/baccarat';

  constructor(private http: HttpClient) { }

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
    return this.http.get<{ playerCards: Card[], bankerCards: Card[] }>(`${this.apiUrl}/cards`);
  }

  // Az utolsó játék eredményének lekérése
  getLastResult(): Observable<string> {
    return this.http.get<string>(`${this.apiUrl}/result`, { responseType: 'text' as 'json' });
  }
}
