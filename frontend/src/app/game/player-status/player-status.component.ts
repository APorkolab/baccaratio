import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-player-status',
  templateUrl: './player-status.component.html',
  styleUrls: ['./player-status.component.scss']
})
export class PlayerStatusComponent implements OnInit {
  balance: number = 1000;
  totalBet: number = 50;
  playerName: string = 'Babiagorai Riparievich Metell';
  newBalance: number = 0;
  editingName = false;
  editingTotalBet = false;
  editingBalance = false;

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.getPlayer();
    this.getPlayerName();
  }

  getPlayer() {
    this.http.get<any>('/baccarat/player').subscribe({
      next: (response) => {
        this.balance = response.chips;
        this.playerName = response.name;
      },
      error: (error) => console.error('Error fetching player:', error)
    });
  }

  updateChips(amount: number) {
    this.http.post<any>('/baccarat/player/chips', { amount }).subscribe({
      next: (response) => {
        console.log(response.message);
        this.balance += amount; // Feltételezve, hogy az összeg lehet negatív is (vesztés esetén)
      },
      error: (error) => console.error('Error updating chips:', error)
    });
  }

  getPlayerName() {
    this.http.get<any>('/baccarat/player/name').subscribe({
      next: (response) => {
        this.playerName = response.name;
      },
      error: (error) => console.error('Error fetching player name:', error)
    });
  }

  setPlayerName(name: string) {
    this.http.put<any>('/baccarat/player/name', { name }).subscribe({
      next: (response) => {
        console.log(response.message);
        this.playerName = name;
      },
      error: (error) => console.error('Error setting player name:', error)
    });
  }

  updateTotalBet(betAmount: number) {
    this.http.post<any>('/baccarat/player/bet', { amount: betAmount }).subscribe({
      next: (response) => {
        console.log(response.message);
        this.totalBet = betAmount;
      },
      error: (error) => console.error('Error updating total bet:', error)
    });
  }

  getChips() {
    this.http.get<any>('/baccarat/player/chips').subscribe({
      next: (response) => {
        this.balance = response.chips;
      },
      error: (error) => console.error('Error fetching chips:', error)
    });
  }


  updateBalance(newBalance: number) {
    this.http.post<any>('/baccarat/player/balance', { newBalance }).subscribe({
      next: (response) => {
        console.log(response.message);
        this.balance = newBalance; // Feltételezve, hogy a backend frissíti az egyenleget
      },
      error: (error) => console.error('Error updating balance:', error)
    });
  }

}
