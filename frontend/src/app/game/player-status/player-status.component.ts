import { ChangeDetectorRef, Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { GameService } from '../game.service';
import { Player } from 'src/app/model/player';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-player-status',
  templateUrl: './player-status.component.html',
  styleUrls: ['./player-status.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule],
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
  currentBet: number = 0;
  isLoading: boolean = false;
  constructor(private gameService: GameService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.getPlayer();
    this.subscription.add(
      this.gameService.balance$.subscribe(newBalance => {
        this.balance = newBalance;
        console.log('Egyenleg frissítve a komponensben:', newBalance); // Debug napló
        this.cdr.detectChanges(); // Manuálisan triggereljük a változásdetektálást
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  updateCurrentBetAmount(amount: number): void {
    this.currentBet = amount;
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
        this.balance = response.chips;
        this.gameService.updateBalance(this.balance);
      },
      error: (error) => console.error('Error updating chips:', error),
    });
  }

  // updateTotalBet(betAmount: number) {
  //   this.gameService.updateTotalBet(betAmount).subscribe({
  //     next: (response: Player) => {
  //       console.log('Total bet updated:', response);
  //       this.gameService.updateBalance(this.newBalance - betAmount);
  //     },
  //     error: (error) => console.error('Error updating total bet:', error),
  //   });
  // }


  getPlayerName(): void {
    this.gameService.getPlayerName().subscribe({
      next: (response: Player) => {
        this.playerName = response.name ?? '';
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


  @Output() showAuthorModalEvent = new EventEmitter<void>();
  @Output() hideAuthorModalEvent = new EventEmitter<void>();

  showAuthorModal(): void {
    this.showAuthorModalEvent.emit();
  }

  hideAuthorModal(): void {
    this.hideAuthorModalEvent.emit();
  }
}
