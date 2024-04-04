import { Component, OnInit } from '@angular/core';
import { GameService } from '../game.service';
import { Player } from 'src/app/model/player';

@Component({
  selector: 'app-player-status',
  templateUrl: './player-status.component.html',
  styleUrls: ['./player-status.component.scss'],
})
export class PlayerStatusComponent implements OnInit {
  balance: number = 1000;
  totalBet: number = 50;
  playerName: string = 'Babiagorai Riparievich Metell';
  newBalance: number = 0;
  editingName = false;
  editingTotalBet = false;
  editingBalance = false;
  currentBetAmount: number = 0;

  constructor(private gameService: GameService) { }

  ngOnInit(): void {
    this.getPlayer();
    this.getPlayerName();
    this.gameService.updateBalance(this.balance);
  }

  updateCurrentBetAmount(amount: number): void {
    this.currentBetAmount = amount;
  }

  getPlayer() {
    this.gameService.getPlayer().subscribe({
      next: (response: Player) => {
        this.balance = response.chips;
        this.playerName = response.name;
      },
      error: (error) => console.error('Error fetching player:', error),
    });
  }

  updateChips(amount: number) {
    this.gameService.updateChips(amount).subscribe({
      next: (response: Player) => {
        console.log(response);
        this.balance += amount;
        this.gameService.updateBalance(this.balance);
      },
      error: (error) => console.error('Error updating chips:', error),
    });
  }

  getPlayerName() {
    this.gameService.getPlayerName().subscribe({
      next: (response: Player) => {
        this.playerName = response.name;
      },
      error: (error) => console.error('Error fetching player name:', error),
    });
  }

  setPlayerName(name: string) {
    this.gameService.setPlayerName(name).subscribe({
      next: (response: { message: string }) => {
        console.log(response.message);
        this.playerName = name;
      },
      error: (error) => console.error('Error setting player name:', error),
    });
  }

  updateTotalBet(betAmount: number) {
    this.gameService.updateTotalBet(betAmount).subscribe({
      next: (response: Player) => {
        console.log(response);
        this.totalBet = betAmount;
        this.gameService.updateBalance(this.balance);
      },
      error: (error) => console.error('Error updating total bet:', error),
    });
  }

  getChips() {
    this.gameService.getChips().subscribe({
      next: (response: Player) => {
        this.balance = response.chips;
        this.gameService.updateBalance(this.balance);
      },
      error: (error) => console.error('Error fetching chips:', error),
    });
  }

  updateBalance(newBalance: number): void {
    this.gameService.updateBalanceOnServer(newBalance).subscribe({
      next: (response) => {
        console.log('Egyensúly frissítve: ', response.message);
      },
      error: (error) => {
        console.error('Hiba az egyensúly frissítése közben: ', error);
      },
    });
  }
}
