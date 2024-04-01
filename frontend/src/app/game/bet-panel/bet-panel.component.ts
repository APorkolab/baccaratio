import { Component, EventEmitter, Output } from '@angular/core';
import { GameService } from '../game.service';
import { firstValueFrom } from 'rxjs';
interface Chip {
  value: number;
  label: string;
}

interface BetOption {
  type: string;
  label: string;
  odds: string;
}

@Component({
  selector: 'app-bet-panel',
  templateUrl: './bet-panel.component.html',
  styleUrls: ['./bet-panel.component.scss']
})
export class BetPanelComponent {
  chips: Chip[] = [
    { value: 1, label: '1' },
    { value: 5, label: '5' },
    { value: 25, label: '25' },
    { value: 100, label: '100' },
    { value: 500, label: '500' },
    { value: 2500, label: '2500' }
  ];
  betHistory: number[] = [];

  betOptions: BetOption[] = [
    { type: 'player', label: 'PLAYER', odds: '1:1' },
    { type: 'banker', label: 'BANKER', odds: '0.95:1' },
    { type: 'tie', label: 'TIE', odds: '8:1' },
    { type: 'pPair', label: 'PLAYER PAIR', odds: '11:1' },
    { type: 'bPair', label: 'BANKER PAIR', odds: '11:1' },
    { type: 'perfectPairOne', label: 'PERFECT PAIR', odds: '25:1' },
  ];

  selectedChip: Chip = this.chips[0];
  @Output() currentBetAmountChanged: EventEmitter<number> = new EventEmitter();
  currentBetAmount: number = 0;

  @Output() betPlaced: EventEmitter<{ type: string; amount: number; }> = new EventEmitter();

  constructor(private gameService: GameService) { }

  selectChip(chip: Chip): void {
    this.selectedChip = chip;
  }

  addToBet(): void {
    this.currentBetAmount += this.selectedChip.value;
    this.betHistory.push(this.selectedChip.value);
    this.currentBetAmountChanged.emit(this.currentBetAmount);
  }

  doubleBet(): void {
    this.currentBetAmount *= 2;
    this.betHistory.push(this.currentBetAmount);
    this.currentBetAmountChanged.emit(this.currentBetAmount);
  }

  undoLastBet(): void {
    const lastBet = this.betHistory.pop();
    if (lastBet) {
      this.currentBetAmount -= lastBet;
      this.currentBetAmountChanged.emit(this.currentBetAmount);
    }
  }


  async placeBet(betType: string): Promise<void> {
    if (this.currentBetAmount > 0) {
      try {
        const response = await firstValueFrom(this.gameService.placeBet(betType, this.currentBetAmount));
        console.log('Bet response:', response);

        const playResponse = await firstValueFrom(this.gameService.playGame());

        setTimeout(() => {
          alert(playResponse);
        }, 5000);

        this.betPlaced.emit({ type: betType, amount: this.currentBetAmount });
        this.currentBetAmount = 0;
        this.currentBetAmountChanged.emit(this.currentBetAmount);
      } catch (error) {
        console.error('Error during betting process:', error);
      }
    } else {
      console.error('No bet amount selected');
    }
  }


}
