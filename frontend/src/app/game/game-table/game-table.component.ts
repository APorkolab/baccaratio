import { Card } from './../../model/card';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BetPanelComponent } from '../bet-panel/bet-panel.component';
import { PlayerStatusComponent } from '../player-status/player-status.component';
import { Subscription } from 'rxjs';
import { GameService } from '../game.service';
import { from } from 'rxjs';
import { concatMap, delay } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-game-table',
  templateUrl: './game-table.component.html',
  styleUrls: ['./game-table.component.scss'],
})
export class GameTableComponent implements OnInit, OnDestroy {
  apiUrl: string = environment.apiUrl;

  handleBetAmountChange(amount: number): void {
    if (this.playerStatusComponent) {
      this.playerStatusComponent.updateCurrentBetAmount(amount);
    }
  }

  playerCards: Card[] = [];
  bankerCards: Card[] = [];
  private subscriptions = new Subscription();
  @ViewChild(BetPanelComponent) betPanelComponent!: BetPanelComponent;
  @ViewChild(PlayerStatusComponent)
  playerStatusComponent!: PlayerStatusComponent;

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

    if (this.betPanelComponent) {
      this.betPanelComponent.betPlaced.subscribe(({ type, amount }) => {
        console.log(`Fogadás megtörtént: ${type} összeggel: ${amount}`);
        this.getCards();
      });
    }
  }

  ngAfterViewInit() {
    this.betPanelComponent.betPlaced.subscribe(({ type, amount }) => {
      console.log(`Fogadás megtörtént: ${type} összeggel: ${amount}`);
      this.getCards();
    });
  }

  getCards(): void {
    this.http
      .get<{ playerCards: Card[]; bankerCards: Card[] }>(`${this.apiUrl}/cards`)
      .subscribe(
        (response) => {
          this.playerCards = [];
          this.bankerCards = [];

          const combinedCards = response.playerCards
            .map((card, i) => ({ card, type: 'player', index: i * 2 }))
            .concat(
              response.bankerCards.map((card, i) => ({
                card,
                type: 'banker',
                index: i * 2 + 1,
              }))
            )
            .sort((a, b) => a.index - b.index);

          from(combinedCards)
            .pipe(concatMap((item) => from([item]).pipe(delay(500))))
            .subscribe((item) => {
              if (item.type === 'player') {
                this.playerCards.push(item.card);
              } else {
                this.bankerCards.push(item.card);
              }
            });
        },
        (error) => {
          console.error(
            'There was an error retrieving the cards from the backend',
            error
          );
        }
      );
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
}
