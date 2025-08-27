import { Card } from './../../model/card';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BetPanelComponent } from '../bet-panel/bet-panel.component';
import { PlayerStatusComponent } from '../player-status/player-status.component';
import { Subscription } from 'rxjs';
import { GameService } from '../game.service';
import { environment } from 'src/environments/environment';
import { CommonModule } from '@angular/common';
import { trigger, transition, query, style, stagger, animate } from '@angular/animations';

@Component({
  selector: 'app-game-table',
  templateUrl: './game-table.component.html',
  styleUrls: ['./game-table.component.scss'],
  standalone: true,
  imports: [CommonModule, PlayerStatusComponent, BetPanelComponent],
  animations: [
    trigger('cardAnimation', [
      transition('* => *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(-50px)' }),
          stagger('150ms',
            animate('500ms cubic-bezier(0.35, 0, 0.25, 1)',
            style({ opacity: 1, transform: 'none' })))
        ], { optional: true })
      ])
    ])
  ]
})
export class GameTableComponent implements OnInit, OnDestroy {
  apiUrl: string = environment.apiUrl;
  playerCardsSubject: any;

  handleBetAmountChange(amount: number): void {
    if (this.playerStatusComponent) {
      this.playerStatusComponent.updateCurrentBetAmount(amount);
    } else {
      console.warn('playerStatusComponent is not initialized');
    }
  }

  playerCards: Card[] = [];
  bankerCards: Card[] = [];
  private subscriptions = new Subscription();
  @ViewChild(BetPanelComponent) betPanelComponent!: BetPanelComponent;
  @ViewChild(PlayerStatusComponent)
  playerStatusComponent!: PlayerStatusComponent;
  isLoading: boolean = false;
  showModal: boolean = false;

  constructor(private http: HttpClient, private gameService: GameService) { }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  ngOnInit() {
    this.subscriptions.add(
      this.gameService.playerCards$.subscribe((cards: Card[]) => {
        this.playerCards = cards;
      })
    );
    this.subscriptions.add(
      this.gameService.bankerCards$.subscribe((cards: Card[]) => {
        this.bankerCards = cards;
      })
    );
  }

  ngAfterViewInit() {
    if (this.betPanelComponent) {
      this.betPanelComponent.betPlaced.subscribe(({ type, amount }) => {
        console.log(`Fogadás megtörtént: ${type} összeggel: ${amount}`);
      });
    }
  }



  getCardImage(card: Card): string {
    if (!card || !card['value'] || !card.suit) {
      return '../../../assets/cards/back.png';
    }

    let value = card['value'].toLowerCase();
    if (value === 'a') {
      value = 'ace';
    } else if (value === 'k') {
      value = 'king';
    } else if (value === 'q') {
      value = 'queen';
    } else if (value === 'j') {
      value = 'jack';
    } else if (value === '10') {
      value = '10';
    }

    const suit = card.suit.toLowerCase();
    let suitName = suit;
    return `../../../assets/cards/${value}_of_${suitName}.png`;
  }

  showAuthorModal(): void {
    this.showModal = true;
  }

  hideModal(): void {
    this.showModal = false;
  }

}