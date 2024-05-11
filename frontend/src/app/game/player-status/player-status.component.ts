import { Component, OnDestroy, OnInit } from '@angular/core';
import { GameService } from '../game.service';
import { Player } from 'src/app/model/player';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-player-status',
  templateUrl: './player-status.component.html',
  styleUrls: ['./player-status.component.scss'],
})
export class PlayerStatusComponent implements OnInit, OnDestroy {
  balance: number = 1000;
  totalBet: number = 50;
  playerName: string = 'Babiagorai Riparievich Metell';
  newBalance: number = 0;
  editingName = false;
  editingTotalBet = false;
  editingBalance = false;
  currentBetAmount: number = 0;
  subscription: Subscription = new Subscription();

  constructor(private gameService: GameService) { }

  ngOnInit(): void {
    this.getPlayer();
    this.getPlayerName();
    this.subscription.add(this.gameService.balance$.subscribe(newBalance => {
      this.balance = newBalance;  // Ellenőrzés, hogy ez a sor meghívódik-e frissítéskor
      console.log('Balance updated to:', newBalance); // Debug üzenet
    }));
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
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
        console.log('Chips updated:', response);
        this.gameService.updateBalance(this.newBalance = response.chips);
      },
      error: (error) => console.error('Error updating chips:', error),
    });
  }

  updateTotalBet(betAmount: number) {
    this.gameService.updateTotalBet(betAmount).subscribe({
      next: (response: Player) => {
        console.log('Total bet updated:', response);
        this.gameService.updateBalance(this.newBalance - betAmount);
      },
      error: (error) => console.error('Error updating total bet:', error),
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

  getChips() {
    this.gameService.getChips().subscribe({
      next: (response: Player) => {
        this.newBalance = response.chips;
        this.gameService.updateBalance(this.newBalance);
      },
      error: (error) => console.error('Error fetching chips:', error),
    });
  }

  updateBalance(newBalance: number): void {
    this.gameService.updateBalanceOnServer(newBalance).subscribe({
      next: (response) => {
        console.log('Balance frissítve: ', response.message);
      },
      error: (error) => {
        console.error('Hiba az egyensúly frissítése közben: ', error);
      },
    });
  }
}
